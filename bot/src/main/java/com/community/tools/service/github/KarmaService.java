package com.community.tools.service.github;

import com.community.tools.model.GitHubComment;
import com.community.tools.model.User;
import com.community.tools.service.github.jpa.MentorsRepository;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestReviewComment;
import org.kohsuke.github.GHReaction;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Deprecated
@Service
@Slf4j
public class KarmaService {

  @Autowired
  private GitHubConnectService service;
  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private MentorsRepository mentorsRepository;

  /**
   * This method will increase karma, if comment is approved.
   *
   * @param traineeReviewer Github login of trainee
   * @param amountOfKarma   amount of karma
   */
  public void changeUserKarma(String traineeReviewer, int amountOfKarma) {
    log.info("Trainee {} gets {} points in karma", traineeReviewer, amountOfKarma);
    if (stateMachineRepository.findByGitName(traineeReviewer).isPresent()
            && !mentorsRepository.findByGitNick(traineeReviewer).isPresent()) {
      User user = stateMachineRepository.findByGitName(traineeReviewer).get();
      user.setKarma(user.getKarma() + amountOfKarma);
      stateMachineRepository.save(user);
    }
  }

  /**
   * This method will change karma, if comment is approved.
   *
   * @param numberOfPull number of pull request
   */
  public void changeKarmaBasedOnReaction(int numberOfPull) {
    try {
      GHRepository repository = service.getGitHubRepository();
      GHPullRequest currentPr = repository.getPullRequest(numberOfPull);


      List<GitHubComment> comments = new ArrayList<>();
      PagedIterable<GHIssueComment> commentsIssue = currentPr.listComments();
      comments.addAll(checkForIssueCommentsApproved(commentsIssue));
      PagedIterable<GHPullRequestReviewComment> commentsReview
              = currentPr.listReviewComments();
      comments.addAll(checkForReviewsCommentsApproved(commentsReview));
      log.info("Total {} comments with approved text added to the {} pull",
              comments.size(), numberOfPull);
      String actorPullRequest = currentPr.getUser().getLogin();
      comments.stream()
              .collect(Collectors.groupingBy(GitHubComment::getAuthorComment))
              .values().stream().map(c -> c.get(0))
              .sorted(Comparator.comparing(GitHubComment::getCreatedAt)).limit(3)
              .forEach(c -> karmaForReaction(c, actorPullRequest));
    } catch (IOException e) {
      log.info("Some happen with connection to Gh", e);
      throw new RuntimeException(e);
    }
  }

  private  List<GitHubComment> checkForIssueCommentsApproved(
          PagedIterable<GHIssueComment> commentsIssue) throws IOException {
    List<GHIssueComment> issueCommentList = commentsIssue.asList().stream()
            .filter(c -> c.getBody().toLowerCase().trim().equals("approved"))
            .collect(Collectors.toList());
    List<GitHubComment> comments = new ArrayList<>();
    for (GHIssueComment issueComment: issueCommentList) {
      GitHubComment comment = new GitHubComment();
      comment.setAuthorComment(issueComment.getUser().getLogin());
      comment.setCreatedAt(issueComment.getCreatedAt());
      comment.setListOfReaction(issueComment.listReactions());
      comments.add(comment);
    }
    return comments;
  }

  private List<GitHubComment> checkForReviewsCommentsApproved(
          PagedIterable<GHPullRequestReviewComment> reviews) throws IOException {
    List<GHPullRequestReviewComment> reviewCommentsList = reviews.asList().stream()
            .filter(c -> c.getBody().toLowerCase().trim().equals("approved"))
            .collect(Collectors.toList());
    List<GitHubComment> comments = new ArrayList<>();
    for (GHPullRequestReviewComment reviewComment: reviewCommentsList) {
      GitHubComment comment = new GitHubComment();
      comment.setAuthorComment(reviewComment.getUser().getLogin());
      comment.setCreatedAt(reviewComment.getCreatedAt());
      comment.setListOfReaction(reviewComment.listReactions());
      comments.add(comment);
    }
    return comments;
  }

  private void karmaForReaction(GitHubComment comment, String actorPullRequest) {
    String typeOfReaction;
    String actorOfReaction;
    String actorOfComment = comment.getAuthorComment();
    log.info("Author pull request {}", actorPullRequest);
    PagedIterable<GHReaction> listOfReaction = comment.getListOfReaction();
    for (GHReaction reaction : listOfReaction) {
      actorOfReaction = reaction.getUser().getLogin();
      typeOfReaction = reaction.getContent().getContent();
      log.info("Type of reaction {} added {} to the comment  of the author {}.",
              typeOfReaction, actorOfReaction, actorOfComment);
      if (typeOfReaction.equals("+1") && !actorOfReaction.equals(actorOfComment)) {
        if (actorPullRequest.equals(actorOfReaction)) {
          changeUserKarma(actorOfComment, 1);
        } else if (mentorsRepository.findByGitNick(actorOfReaction).isPresent()) {
          changeUserKarma(actorOfComment, 2);
        }
      } else if (typeOfReaction.equals("-1")
              && mentorsRepository.findByGitNick(actorOfReaction).isPresent()) {
        changeUserKarma(actorOfComment, -1);
      }
    }
  }
}
