package com.community.tools.service.github;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import com.community.tools.discord.DiscordService;
import com.community.tools.dto.GithubRepositoryDto;
import com.community.tools.dto.GithubUserDto;
import com.community.tools.model.EmailBuild;
import com.community.tools.model.TaskStatus;
import com.community.tools.service.EmailService;
import com.community.tools.service.github.util.RepositoryNameService;
import com.community.tools.service.github.util.dto.ParsedRepositoryName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestReview;
import org.kohsuke.github.GHPullRequestReviewState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GHWorkflowRun;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassroomServiceImpl implements ClassroomService {

  private final GitHub gitHub;
  private final String mainOrganizationName;
  private final String traineeshipOrganizationName;
  private final String traineesTeamName;
  private final String classroomWorkflow;
  private final String completedTaskLabel;
  private final String defaultPullRequestName;
  private final RepositoryNameService repositoryNameService;

  @Value("${millisecondsIntervalForClassroomParse}")
  private int classroomMillisecondsInterval;

  @Value("${readyForReviewChannel}")
  private String reviewChannel;

  @Value("${email.notification.enabled}")
  private boolean isEmailEnabled;

  private DiscordService discordService;

  @Autowired
  public void setDiscordService(DiscordService discordService) {
    this.discordService = discordService;
  }

  @Autowired
  private EmailService emailService;

  /**
   * Builds ClassroomServiceImpl object with given GitHub API client and GitHub properties.
   *
   * @param gitHub                      GitHub API client
   * @param mainOrganizationName        name of the main GitHub organization
   * @param traineeshipOrganizationName name of the GitHub organization for trainees
   * @param traineesTeamName            name of the team to add new trainees to
   * @param classroomWorkflow           name of the workflow in trainees repositories
   * @param completedTaskLabel          label which marks that task is completed
   * @param defaultPullRequestName      name of the default pull request in trainees repositories
   * @param repositoryNameService       RepositoryNameService instance
   */
  @Autowired
  public ClassroomServiceImpl(GitHub gitHub,
      @Value("${github.main-organization-name}") String mainOrganizationName,
      @Value("${github.traineeship-organization-name}") String traineeshipOrganizationName,
      @Value("${github.teams.trainees}") String traineesTeamName,
      @Value("${github.workflows.classroom}") String classroomWorkflow,
      @Value("${github.labels.completed-task}") String completedTaskLabel,
      @Value("${github.pull-requests.default}") String defaultPullRequestName,
      RepositoryNameService repositoryNameService) {

    this.gitHub = gitHub;
    this.mainOrganizationName = mainOrganizationName;
    this.traineeshipOrganizationName = traineeshipOrganizationName;
    this.traineesTeamName = traineesTeamName;
    this.classroomWorkflow = classroomWorkflow;
    this.completedTaskLabel = completedTaskLabel;
    this.defaultPullRequestName = defaultPullRequestName;
    this.repositoryNameService = repositoryNameService;
  }

  /**
   * Adds user with passed GitHub login to the organization's trainees team.
   *
   * @param gitName github login
   */
  @SneakyThrows
  @Override
  public void addUserToTraineesTeam(String gitName) {
    GHUser user = gitHub.getUser(gitName);

    GHOrganization organization = gitHub.getMyOrganizations().get(mainOrganizationName);
    GHTeam traineesTeam = organization.getTeamByName(traineesTeamName);

    traineesTeam.add(user);
  }

  /**
   * Fetches users, who made at least one commit during the passed period and returns information
   * about them and their repositories.
   *
   * @param period period
   * @return information about users and their repositories
   */
  @SneakyThrows
  @Override
  public List<GithubUserDto> getAllActiveUsers(Period period) {
    GHOrganization organization = gitHub
        .getMyOrganizations()
        .get(traineeshipOrganizationName);
    Map<String, List<FetchedRepository>> allUserRepositories =
        fetchAllUserRepositories(organization);

    Date startDate = convertToDate(LocalDate
        .now()
        .minus(period));
    allUserRepositories
        .entrySet()
        .removeIf(entry -> {
          List<FetchedRepository> userRepositories = entry.getValue();
          return userRepositories.stream().noneMatch(fetchedRepository -> fetchedRepository
              .getLastCommitDate()
              .after(startDate)
          );
        });

    return allUserRepositories
        .entrySet()
        .parallelStream()
        .map(entry -> buildGithubUserDto(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  private Map<String, List<FetchedRepository>> fetchAllUserRepositories(GHOrganization organization)
      throws IOException {
    return organization
        .getRepositories()
        .entrySet()
        .parallelStream()
        .filter(entry -> repositoryNameService.isPrefixedWithTaskName(entry.getKey()))
        .map(entry -> {
          GHRepository repository = entry.getValue();
          try {
            Date lastCommitDate = repository
                .queryCommits()
                .pageSize(1)
                .list()
                .iterator()
                .next()
                .getCommitDate();

            return new FetchedRepository(repository, lastCommitDate);
          } catch (IOException e) {
            log.warn("failed to fetch last commit date", e);
            return new FetchedRepository(repository, null);
          }
        })
        .filter(fetchedRepository -> fetchedRepository.getLastCommitDate() != null)
        .collect(groupingBy(fetchedRepository -> {
          ParsedRepositoryName parsedName = repositoryNameService.parseRepositoryName(
              fetchedRepository.getRepository().getName());
          return parsedName.getCreatorGitName();
        }));
  }

  /**
   * Scheduled method to handle notifications and review requests for repositories in the
   * traineeship organization. This method retrieves the repositories from the organization, checks
   * their workflows' statuses, and processes pull requests accordingly.
   * <p>
   * If a task is not assigned, a link to the pull request is sent to the 'ready-for-review'
   * channel. If at least one reviewer has subscribed, send them an email notification, provided
   * that the build has passed and the 'ready for review' tag is set. If the build is unsuccessful
   * and more than a day has passed, an auto-comment is added indicating that tasks with failed
   * builds will not be reviewed.
   * </p>
   *
   * @throws IOException If an I/O error occurs during the GitHub API calls.
   */
  @Scheduled(fixedRateString = "${millisecondsIntervalForClassroomParse}")
  public void handleNotifications() throws IOException {
    Date currentTime = new Date();

    GHOrganization organization = gitHub.getMyOrganizations().get(traineeshipOrganizationName);
    Map<String, GHRepository> repositories = organization.getRepositories();

    for (Map.Entry<String, GHRepository> entry : repositories.entrySet()) {
      GHRepository repository = entry.getValue();

      Optional<GHWorkflowRun> lastWorkflow = getWorkflowRun(repository);
      GHWorkflowRun.Conclusion lastWorkflowConclusion =
          !lastWorkflow.isPresent()
              ? GHWorkflowRun.Conclusion.FAILURE : lastWorkflow.get().getConclusion();

      List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);
      for (GHPullRequest pullRequest : pullRequests) {

        GHCommit lastCommit = pullRequest.getHead().getCommit();
        Date lastCommitDate = lastCommit.getCommitDate();

        List<GHPullRequestReview> listReviews = pullRequest.listReviews().toList();
        GHPullRequestReview lastReview = listReviews.isEmpty() ? null :
            listReviews.get(listReviews.size() - 1);

        TaskStatus taskStatus = determineTaskStatus(lastWorkflowConclusion, repository,
            listReviews, currentTime, lastCommitDate, lastReview);

        processTaskStatus(repository, pullRequest, taskStatus, lastReview, lastCommit);
      }
    }
  }

  private TaskStatus determineTaskStatus(GHWorkflowRun.Conclusion lastWorkflowConclusion,
      GHRepository repository,
      List<GHPullRequestReview> listReviews,
      Date currentTime, Date lastCommitDate,
      GHPullRequestReview lastReview) throws IOException {

    boolean hasReviewers = !listReviews.isEmpty();
    if (lastWorkflowConclusion == GHWorkflowRun.Conclusion.FAILURE) {
      long dayInMillis = 24 * 60 * 60 * 1000;
      if (containsLabel(repository, TaskStatus.READY_FOR_REVIEW.getDescription())
          && isOverTimeInterval(currentTime, lastCommitDate, dayInMillis)
          && !isOverTimeInterval(currentTime, lastCommitDate,
          dayInMillis + classroomMillisecondsInterval)) {
        return TaskStatus.FAILURE;
      }
    } else if (containsLabel(repository, TaskStatus.READY_FOR_REVIEW.getDescription())
        && !isOverTimeInterval(currentTime, lastCommitDate,
        classroomMillisecondsInterval)) {
      if (hasReviewers) {
        if (lastReview.getState() != GHPullRequestReviewState.APPROVED) {
          Date lastReviewDate = lastReview.getSubmittedAt();
          if (lastReviewDate.compareTo(lastCommitDate) > 0) {
            return TaskStatus.CHANGES_REQUESTED;
          } else {
            return TaskStatus.READY_FOR_REVIEW;
          }
        } else {
          return TaskStatus.DONE;
        }
      } else {
        return TaskStatus.NEW;
      }
    }
    return TaskStatus.UNDEFINED;
  }

  private void processTaskStatus(GHRepository repository, GHPullRequest pullRequest,
      TaskStatus taskStatus, GHPullRequestReview lastReview,
      GHCommit lastCommit) throws IOException {

    switch (taskStatus) {
      case FAILURE:
        pullRequest.comment("Tasks with failed builds will not be reviewed.");
        removeAllLabels(repository);
        pullRequest.addLabels(TaskStatus.FAILURE.getDescription());
        break;
      case NEW:
        sendReviewToDiscord(pullRequest.getHtmlUrl().toString());
        break;
      case READY_FOR_REVIEW:
        if (isEmailEnabled) {
          try {
            String reviewerEmail = getGitHubUserEmail(lastReview.getUser().getLogin());
            if (reviewerEmail != null) {
              sendNewCommitEmail(lastReview.getUser(), lastCommit,
                  pullRequest.getHtmlUrl().toString(), reviewerEmail);
            } else {
              log.warn("Reviewer email not found");
            }
          } catch (NullPointerException e) {
            throw new RemoteException("Error sending email");
          }
        }
        break;
      case CHANGES_REQUESTED:
        removeAllLabels(repository);
        pullRequest.addLabels(TaskStatus.CHANGES_REQUESTED.getDescription());
        break;
      case DONE:
        removeAllLabels(repository);
        pullRequest.addLabels(TaskStatus.DONE.getDescription());
        break;
      default:
        break;
    }
  }

  private void removeAllLabels(GHRepository repository) throws IOException {
    List<String> labels = getLabels(repository);
    for (String label : labels) {
      repository.getPullRequests(GHIssueState.OPEN)
          .get(0).removeLabel(label);
    }
  }

  private boolean containsLabel(GHRepository repository, String searchedLabel) {
    List<String> labels = getLabels(repository);
    for (String label : labels) {
      if (label.equals(searchedLabel)) {
        return true;
      }
    }
    return false;
  }

  private boolean isOverTimeInterval(Date currentTime, Date commitDate,
      long timeIntervalInMillis) {

    long timeDifference = currentTime.getTime() - commitDate.getTime();
    return timeDifference > timeIntervalInMillis;
  }

  private void sendReviewToDiscord(String message) {
    discordService.sendMessageToConversation(reviewChannel, message);
  }

  private void sendNewCommitEmail(GHUser reviewer, GHCommit commit,
      String requestLink, String reviewerEmail)
      throws IOException {
    String mailText = "Hi, " + reviewer.getLogin()
        + "! New commit is ready for review. <br/><br/>"
        + "Link: " + requestLink + "<br/>"
        + "Author: " + commit.getAuthor().getLogin() + "<br/>"
        + "Date: " + commit.getCommitDate();
    emailService.sendEmail(EmailBuild.builder()
        .userEmail(reviewerEmail)
        .subject("Check New Commit")
        .text(mailText)
        .build());
  }

  private String getGitHubUserEmail(String username) throws IOException {
    String apiUrl = String.format("https://api.github.com/users/%s/events/public", username);

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(apiUrl);

    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        String jsonResponse = EntityUtils.toString(entity);
        JSONArray events = new JSONArray(jsonResponse);

        for (int i = 0; i < events.length(); i++) {
          JSONObject event = events.getJSONObject(i);
          if ("PushEvent".equals(event.getString("type"))) {
            JSONArray commits = event.getJSONObject("payload").getJSONArray("commits");
            JSONObject commit = commits.getJSONObject(0);
            JSONObject author = commit.getJSONObject("author");
            return author.getString("email");
          }
        }
      }
    }
    return null;
  }

  private GithubUserDto buildGithubUserDto(String creatorGitName,
      List<FetchedRepository> fetchedRepositories) {
    fetchedRepositories.sort(comparing(FetchedRepository::getLastCommitDate).reversed());

    LocalDate lastCommitDate = convertToLocalDate(fetchedRepositories.get(0).getLastCommitDate());

    List<GithubRepositoryDto> repositories = fetchedRepositories
        .stream()
        .map(this::buildGithubRepositoryDto)
        .collect(Collectors.toList());

    return GithubUserDto.builder()
        .gitName(creatorGitName)
        .lastCommit(lastCommitDate)
        .repositories(repositories)
        .totalPoints(getTotalPoints(repositories))
        .completedTasks(getCompletedTasks(repositories))
        .build();
  }

  private int getCompletedTasks(List<GithubRepositoryDto> repositories) {
    return (int) repositories
            .stream()
            .flatMap(repository -> repository.labels().stream())
            .map(String::toLowerCase)
            .filter(label -> label.equals(completedTaskLabel))
            .count();
  }

  private int getTotalPoints(List<GithubRepositoryDto> repositories) {
    return repositories
            .stream()
            .map(GithubRepositoryDto::points)
            .filter(points -> points >= 0)
            .reduce(0, Integer::sum);
  }

  private GithubRepositoryDto buildGithubRepositoryDto(FetchedRepository fetchedRepository) {
    GHRepository repository = fetchedRepository.getRepository();

    String repositoryName = repository.getName();

    ParsedRepositoryName parsedRepositoryName =
        repositoryNameService.parseRepositoryName(repository.getName());

    GHWorkflowRun workflowRun = getWorkflowRun(repository).orElse(null);

    return GithubRepositoryDto.builder()
        .repositoryName(repositoryName)
        .taskName(parsedRepositoryName.getTaskName())
        .lastBuildStatus(getLastBuildStatus(workflowRun))
        .labels(getLabels(repository))
        .points(getPoints(workflowRun))
        .createdAt(getCreatedAt(repository))
        .updatedAt(convertToLocalDate(fetchedRepository.getLastCommitDate()))
        .pullUrl(getLastPullUrl(repository))
        .build();
  }

  private String getLastPullUrl(GHRepository repository) {
    try {
      List<GHPullRequest> allPullRequests = repository.getPullRequests(GHIssueState.ALL);

      if (!allPullRequests.isEmpty()) {
        GHPullRequest lastPullRequest = allPullRequests.get(allPullRequests.size() - 1);
        return lastPullRequest.getHtmlUrl().toString();
      }
    } catch (IOException e) {
      log.warn("failed to fetch pull url", e);
    }
    return null;
  }

  private Optional<GHWorkflowRun> getWorkflowRun(GHRepository repository) {
    try {
      log.info(repository.getName());
      Iterator<GHWorkflowRun> workflowRunIterator = repository
          .getWorkflow(classroomWorkflow)
          .listRuns()
          .withPageSize(1)
          .iterator();
      if (workflowRunIterator.hasNext()) {
        return Optional.of(workflowRunIterator.next());
      } else {
        return Optional.empty();
      }
    } catch (IOException e) {
      log.warn("failed to fetch workflow run", e);
      return Optional.empty();
    }
  }

  private String getLastBuildStatus(GHWorkflowRun workflowRun) {
    if (workflowRun == null) {
      return "";
    }

    return workflowRun
        .getConclusion()
        .toString();
  }

  private int getPoints(GHWorkflowRun workflowRun) {
    if (workflowRun == null) {
      return -1;
    }

    try {
      return workflowRun
          .listJobs()
          .toList()
          .get(0)
          .downloadLogs(in -> new BufferedReader(new InputStreamReader(in)))
          .lines()
          .filter(line -> line.contains("Points"))
          .map(line -> {
            String[] lineSplit = line.split(" ");
            String[] points = lineSplit[2].split("/");
            return Integer.parseInt(points[0]);
          })
          .findFirst()
          .orElse(0);
    } catch (IOException e) {
      log.warn("failed to fetch points from workflow run logs", e);
      return -1;
    }
  }

  private List<String> getLabels(GHRepository repository) {
    try {
      List<GHPullRequest> openPullRequests = repository.getPullRequests(GHIssueState.OPEN);
      for (GHPullRequest pullRequest : openPullRequests) {
        if (pullRequest.getTitle().equals(defaultPullRequestName)) {
          return pullRequest.getLabels()
              .stream()
              .map(GHLabel::getName)
              .collect(Collectors.toList());
        }
      }

      return Collections.emptyList();
    } catch (IOException e) {
      log.warn("failed to fetch labels", e);
      return Collections.emptyList();
    }
  }

  private LocalDate getCreatedAt(GHRepository repository) {
    try {
      return convertToLocalDate(repository.getCreatedAt());
    } catch (IOException e) {
      log.warn("failed to fetch repository creation date", e);
      return null;
    }
  }

  private Date convertToDate(LocalDate date) {
    return Date
        .from(date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant());
  }

  private LocalDate convertToLocalDate(Date date) {
    return date
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  @Getter
  @AllArgsConstructor
  private static class FetchedRepository {

    private final GHRepository repository;
    private final Date lastCommitDate;
  }
}

