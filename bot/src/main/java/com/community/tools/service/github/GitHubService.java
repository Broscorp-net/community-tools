package com.community.tools.service.github;

import static com.community.tools.model.Event.COMMENT;
import static com.community.tools.model.Event.COMMIT;
import static com.community.tools.model.Event.PULL_REQUEST_CLOSED;
import static com.community.tools.model.Event.PULL_REQUEST_CREATED;
import static java.util.Comparator.comparing;
import static org.kohsuke.github.GHIssueState.CLOSED;

import com.community.tools.model.EventData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHPullRequestReviewComment;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.PagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Deprecated
@Service
@RequiredArgsConstructor
public class GitHubService {
  @Autowired
  private final GitHubConnectService service;

  /**
   * Get GitHub pull requests according to state.
   *
   * @param statePullRequest state of pull. T - open, F - closed
   * @return Map of GH login trainee as a key, title of pull as value
   */
  public Map<String, String> getPullRequests(boolean statePullRequest) {
    Map<String, String> listUsers = new HashMap<>();
    try {
      GHRepository repository = service.getGitHubRepository();
      List<GHPullRequest> pullRequests;
      if (!statePullRequest) {
        pullRequests = repository.getPullRequests(GHIssueState.CLOSED);
      } else {
        pullRequests = repository.getPullRequests(GHIssueState.OPEN);
      }

      for (GHPullRequest repo : pullRequests) {
        String login = repo.getUser().getLogin();
        String title = repo.getTitle();
        listUsers.put(login, title);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return listUsers;
  }

  /**
   * Get all events by the date interval.
   *
   * @param startDate startDate
   * @param endDate   endDate
   * @return list of EventData by the date interval
   */
  public List<EventData> getEvents(Date startDate, Date endDate) {
    try {
      GHRepository repository = service.getGitHubRepository();
      Set<EventData> listEvents = new TreeSet<>(comparing(EventData::getCreatedAt));

      List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.ALL);
      for (GHPullRequest pullRequest : pullRequests) {
        Date createdAt = pullRequest.getCreatedAt();
        Date closedAt = pullRequest.getClosedAt();
        String actorPullRequest = pullRequest.getUser().getLogin();
        GHIssueState state = pullRequest.getState();

        if (createdAt.after(startDate) && createdAt.before(endDate)) {
          if (state.equals(CLOSED)) {
            listEvents.add(new EventData(closedAt, actorPullRequest, PULL_REQUEST_CLOSED));
          }
          listEvents.add(new EventData(createdAt, actorPullRequest, PULL_REQUEST_CREATED));
        }

        PagedIterable<GHPullRequestReviewComment> comments = pullRequest.listReviewComments();
        for (GHPullRequestReviewComment comment : comments) {
          Date commentCreatedAt = comment.getCreatedAt();
          String loginComment = comment.getUser().getLogin();
          boolean periodComment =
                  commentCreatedAt.after(startDate) && commentCreatedAt.before(endDate);
          if (periodComment) {
            listEvents.add(new EventData(commentCreatedAt, loginComment, COMMENT));
          }
        }

        PagedIterable<GHPullRequestCommitDetail> commits = pullRequest.listCommits();
        for (GHPullRequestCommitDetail commit : commits) {
          Date dateCommit = commit.getCommit().getAuthor().getDate();

          boolean periodCommit = dateCommit.after(startDate) && dateCommit.before(endDate);
          if (periodCommit) {
            listEvents.add(new EventData(dateCommit, actorPullRequest, COMMIT));
          }
        }
      }

      return new ArrayList<>(listEvents);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public GHUser getUserByLoginInGitHub(String gitHubLogin) throws IOException {
    return service.getGitHubConnection().getUser(gitHubLogin);
  }

  /**
   * Get all GitHub Collaborators.
   *
   * @return Set of GH Users
   */
  public Set<GHUser> getGitHubAllUsers() {
    try {
      GHRepository repository = service.getGitHubRepository();
      return repository.getCollaborators();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get active GitHub user names.
   *
   * @param date date
   * @return Set of GH User names
   */
  public Set<String> getActiveUsersFromGit(Date date) {
    Set<String> names = new HashSet<>();
    try {
      GHRepository repository = service.getGitHubRepository();
      List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.ALL);

      for (GHPullRequest pr : pullRequests) {
        if (pr.getCreatedAt().after(date)) {
          names.add(pr.getUser().getLogin());
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return names;
  }

}