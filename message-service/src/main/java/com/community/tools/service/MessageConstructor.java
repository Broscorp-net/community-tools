package com.community.tools.service;

import com.community.tools.model.EventData;
import java.util.List;

public interface MessageConstructor<T> {

  T createGetFirstTaskMessage(String availableNickMessage, String getFirstTaskMessage,
      String linkFirstTaskMessage);

  T createAddGitNameMessage(String addGitNameMessage);

  T createNoOneCaseMessage(String noOneCaseMessage);

  T createNotThatMessage(String noThatMessage);

  T createAbilityReviewMessage(String abilityReviewMessage);

  T createFirstQuestion(String firstQuestion);

  T createSecondQuestion(String secondQuestion);

  T createThirdQuestion(String thirdQuestion);

  T createMessageAboutRules(String firstRule, String secondRule, String thirdRule,
      String fourthRule);

  T createErrorWithAddingGitNameMessage(String errorMessage);

  T createEstimateTheTaskMessage(String header, String[] estimateQuestions, String footer);

  T createMessageAboutSeveralInfoChannel(String[] infoChannelMessages);

  T createFailedBuildMessage(String url, String task);

  T createInfoLinkMessage(String info, String url, String img);

  T createStatisticMessage(List<EventData> events);

  T createNextTaskMessage(List<String> tasksList, int numberTask);

}
