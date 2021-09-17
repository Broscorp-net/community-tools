package com.community.tools.slack;

import com.community.tools.model.Event;
import com.community.tools.model.EventData;
import com.community.tools.service.MessageConstructor;

import com.community.tools.util.MessageUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("slack")
public class MessagesToSlack implements MessageConstructor<String> {

  @Override
  public String createGetFirstTaskMessage(
      String availableNickMessage, String getFirstTaskMessage, String linkFirstTaskMessage) {
    return "[{\"type\": \"section\",\"text\": {"
      + "\"type\":\"mrkdwn\",\"text\": \""
      + availableNickMessage
      + "\n\n"
      + getFirstTaskMessage
      + " <"
      + linkFirstTaskMessage
      + "|TASK> :link:\"}}]";
  }

  @Override
  public String createAddGitNameMessage(String addGitNameMessage) {
    return "[{\"type\": \"section\",\"text\": {"
      + "\"type\":\"mrkdwn\",\"text\": \""
      + addGitNameMessage
      + "\"}}]";
  }

  @Override
  public String createNoOneCaseMessage(String noOneCaseMessage) {
    return "[{\"type\": \"divider\"},{\"type\": \"section\","
      + "\"text\": {\"type\": \"mrkdwn\",\"text\": \""
      + noOneCaseMessage
      + "\"},"
      + "\"accessory\": {\"type\": \"button\",\"text\": {\"type\": \"plain_text\","
      + "\"text\": \"Button\",\"emoji\": true},\"value\": \"Button\"}}]";
  }

  @Override
  public String createNotThatMessage(String noThatMessage) {
    return "[{\"type\": \"section\",\"text\": {"
      + "\"type\": \"mrkdwn\",\"text\": \""
      + noThatMessage
      + "\"}}]";
  }

  @Override
  public String createAbilityReviewMessage(String abilityReviewMessage) {
    return "[{\"type\": \"context\","
      + " \"elements\": [{\"type\": \"mrkdwn\",\"text\": \""
      + abilityReviewMessage
      + "\"}]}]";
  }

  @Override
  public String createFirstQuestion(String firstQuestion) {
    return "[ {\"type\": \"context\", \"elements\": [{"
      + " \"type\": \"mrkdwn\", \"text\": \"```"
      + firstQuestion
      + "```\" } ] } ]";
  }

  @Override
  public String createSecondQuestion(String secondQuestion) {
    return "[ {\"type\": \"context\", \"elements\": [{"
      + " \"type\": \"mrkdwn\", \"text\": \"```"
      + secondQuestion
      + "```\" } ] } ]";
  }

  @Override
  public String createThirdQuestion(String thirdQuestion) {
    return "[ {\"type\": \"context\", \"elements\": [{"
      + " \"type\": \"mrkdwn\", \"text\": \"```"
      + thirdQuestion
      + "```\" } ] } ]";
  }

  @Override
  public String createMessageAboutRules(
      String firstRule, String secondRule, String thirdRule, String fourthRule) {
    return "[{ \"type\": \"section\", \"text\": {"
      + " \"type\": \"mrkdwn\", \"text\": \""
      + firstRule
      + "\" }}, {"
      + " \"type\": \"section\", \"text\": { \"type\": \"mrkdwn\", \"text\": \"<"
      + secondRule
      + "|Rules> :link:\" } }, { \"type\": \"section\","
      + " \"text\": {\"type\": \"mrkdwn\", \"text\": \""
      + thirdRule
      + "\"} }, {\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \""
      + fourthRule
      + "\"}]}]";
  }

  @Override
  public String createErrorWithAddingGitNameMessage(String errorMessage) {
    return "[{\"type\": \"section\",\"text\":"
      + " {\"type\": \"mrkdwn\",\"text\": \""
      + errorMessage
      + "\n*<https://broscorp-community.slack.com/archives/D01QZ9U2GH5|Liliya Stepanovna>*\"}}]";
  }

  @Override
  public String createEstimateTheTaskMessage(
      String header, String[] estimateQuestions, String footer) {
    StringBuilder builder = new StringBuilder();
    builder.append("[ {\"type\": \"section\",\"text\": {\"type\": \"mrkdwn\",\"text\": \"");
    builder.append(header).append("\n");
    Arrays.stream(estimateQuestions).forEachOrdered(que -> builder.append(que).append("\n"));
    builder.append("\n *").append(footer).append("*\"}}]");

    return builder.toString();
  }

  @Override
  public String createMessageAboutSeveralInfoChannel(String[] infoChannelMessages) {
    StringBuilder builder =
        new StringBuilder(
          "[{\"type\": \"header\",\"text\": {\"type\": \"plain_text\",\"text\": \"");
    for (int i = 0; i < infoChannelMessages.length; i++) {
      builder.append(infoChannelMessages[i]).append(INFO_MESSAGES_SLACK[i]);
    }
    return builder.toString();
  }

  @Override
  public String createFailedBuildMessage(String url, String task) {
    return String.format(
      "[{\"type\": \"section\", \"text\": { \"type\": \"mrkdwn\", \"text\": "
        + "\"Oops, your build at the task <%s|%s> is down!\"}}]",
      url, task);
  }

  @Override
  public String createInfoLinkMessage(String info, String url, String img) {
    return String.format(INFO_LINK_MESSAGE, info, url, img);
  }

  @Override
  public String createStatisticMessage(List<EventData> events) {
    StringBuilder messageBuilder = new StringBuilder();

    Map<String, List<EventData>> sortedMapGroupByActors = new HashMap<>();
    events.stream().filter(ed -> !sortedMapGroupByActors.containsKey(ed.getActorLogin()))
      .forEach(ed -> sortedMapGroupByActors.put(ed.getActorLogin(), new ArrayList<>()));

    messageBuilder.append("[{\"type\": \"header\",\t\"text\": {\"type\":"
        + " \"plain_text\",\"text\": \"Statistic:\"}},"
        + "{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\", \"text\": \"");
    events.stream()
        .collect(Collectors.groupingBy(EventData::getType))
        .entrySet().stream()
        .sorted(Comparator
          .comparingInt((Entry<Event, List<EventData>> entry)
              -> entry.getValue().size()).reversed())
        .forEach(entry -> {
          entry.getValue().forEach(e -> sortedMapGroupByActors.get(e.getActorLogin()).add(e));
          messageBuilder.append("\n");
          messageBuilder.append(MessageUtils.getTypeTitleBold(entry.getKey()))
            .append(MessageUtils.emojiGen(entry.getKey()));
          messageBuilder.append(": ");
          messageBuilder.append(entry.getValue().size());
        });

    messageBuilder.append("\"\t}]},{\"type\": \"header\",\"text\": "
        + "{\"type\": \"plain_text\",\"text\": \"Activity:\"}}");
    sortedMapGroupByActors.entrySet().stream()
        .sorted(Comparator
        .comparingInt((Entry<String, List<EventData>> entry)
            -> entry.getValue().size()).reversed())
        .forEach(name -> {
          StringBuilder authorsActivMessage = new StringBuilder();
          name.getValue()
              .forEach(eventData -> {
                authorsActivMessage.append(MessageUtils.emojiGen(eventData.getType()));
              });
          messageBuilder.append(",{\"type\": \"context\",\n"
              + "\"elements\": [{\"type\": \"mrkdwn\",\t\"text\": \"*");

          messageBuilder.append(name.getKey());
          messageBuilder.append("*: ");
          messageBuilder.append(authorsActivMessage);
          messageBuilder.append("\"}]}");
        });
    messageBuilder.append("]");

    return messageBuilder.toString();
  }

  @Override
  public String createNextTaskMessage(List<String> tasksList, int numberTask) {
    return NEXT_TASK + tasksList.get(numberTask) + "|TASK>.\"}}]";
  }

  public static final String STATISTIC =
      "[{\"type\": \"header\",\t\"text\": {"
      + "\"type\":\"plain_text\",\"text\": \"Statistic:\"}},{\"type\": \"context\","
      + "\"elements\":"
      + " [{\"type\": \"mrkdwn\",\"text\": \"";
  public static final String ACTIVITY =
      "\"\t}]},{\"type\": \"header\",\"text\": {"
      + "\"type\": \"plain_text\",\"text\": \"Activity:\"}}";
  public static final String FINISH_PUBLISH_WEEK_STATS =
      ",{\"type\": \"context\",\n\"elements\":" + " [{\"type\": \"mrkdwn\",\t\"text\": \"*";
  public static final String INFO_LINK_MESSAGE =
      "[{\"type\": \"section\", \"text\": "
      + "{\"type\": \"mrkdwn\",\"text\": \"%s \"},"
      + "\"accessory\": {\"type\": \"button\",\"text\": {\"type\": \"plain_text\",\"text\": "
      + "\":loudspeaker:\",\"emoji\": true},\"value\": \"click_me_123\", \"url\": \"%s\","
      + "\"action_id\": \"button-action\"}},{\"type\": \"image\",\"image_url\": \"%s\","
      + "\"alt_text\": \"inspiration\"}]";
  public static final String NEXT_TASK =
      "[{\"type\": \"section\",\"text\":"
      + " {\"type\": \"mrkdwn\",\"text\": \"Here is your next"
      + " <https://github.com/Broscorp-net/traineeship/tree/master/module1/src/main/java/net/broscorp/";
  public static String[] INFO_MESSAGES_SLACK = {
    "\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#welcome \n ",
    "\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#help \n ",
    "\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#general \n",
    "\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#random \n",
    "\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"#hall-of-fame\n",
    "\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"",
    "\"}]}]"
  };
}
