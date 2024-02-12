package com.community.tools.model;

import com.google.common.base.Splitter;
import java.util.Map;

public class Messages {

  // messages for bot
  public static final String ANSWERED_NO_DURING_VERIFICATION =
      "Oops, sorry :neutral_face:"
              + " Check your nickname correctly and try again, please :hugging_face:";
  public static final String ASK_ABOUT_PROFILE = "Is it your profile? (*yes*/*no*)";
  public static final String CHECK_NICK_NAME = "Okay! Let me check your nick,";
  public static final String CONGRATS_AVAILABLE_NICK =
      "Hurray! Your nick is available. Nice to meet you :smile:";
  public static final String DO_NOT_UNDERSTAND_WHAT_TODO =
      "I do not understand what you want," + " please call the admin!";
  public static final String FAILED_CHECK_NICK_NAME =
      "Sry but looks like you are still not added" + " to our team in Git :worried:";
  public static final String FAILED_NICK_NAME =
      "Sry but looks like you are not registered" + " on Github :worried:";
  public static final String GET_FIRST_TASK = "This is your first";
  public static final String LINK_FIRST_TASK =
      "https://github.com/Broscorp-net/traineeship/tree/"
          + "master/module1/src/main/java/net/broscorp/checkstyle";
  public static final String LAST_TASK =
      "You are so cool! :tada:\nJust one step away from"
          + " finishing your set of tasks.\nMake your last task, send it and wait for the review."
          + "\nAlso you can help your colleagues on their journey :hugging_face:";
  public static final String USERS_AGREE_MESSAGE = "I agree";
  public static final String WELCOME = "Welcome to the club buddy :handshake: \n"
      + "Before you start, must write about yourself in channel '#welcome'!";
  public static final String MESSAGE_NOT_WELCOME = "You didn't write about yourself in channel"
      + " #welcome :ghost: \n";
  public static final String WELCOME_CHANNEL = "welcome channel";
  public static final String ADD_GIT_NAME =
      "So, that's it. You've read through all our rules."
          + " \n If you're ready to accept this challenge enter your GitHub nickname.";
  public static final String NO_ONE_CASE = "NO ONE CASE";
  public static final String NOT_THAT_MESSAGE = "Please answer the latest message :ghost:";
  public static final String ABILITY_REVIEW_MESSAGE =
      "Well done! Now you can review tasks your"
          + " colleagues :sunglasses: \n You can leave comments on those tasks where you have"
          + " the marking label done (the task is accepted).\n\n If you think that it is necessary"
          + " to make changes, write a comment and install the label *changes request*."
          + " When you and your colleague decide that the changes applied and the task is done, "
          + "you should write a comment *approved*. \n\n"
              + "If in your opinion everything is ok to write"
          + " a comment approved. You can specify what was done well. :relaxed:  \n"
          + " For the quality reviews, you can get + into karma :smirk:";
  public static final String ZERO_POINTS_MESSAGE =
      "For the task 'pull_name' you got 0 points."
          + " You made a mistake in the name of the pull request, from now on be more careful.";
  public static final String DEFAULT_MESSAGE =
      "Oops, I'm sorry, but I don't have an answer" + " to your request.";
  public static final String FIRST_QUESTION =
      "1. What names should branches" + " and pull requests have?";
  public static final String SECOND_QUESTION = "2. Which library should be used for unit testing?";
  public static final String THIRD_QUESTION =
      "3. Your task labelled as 'changes requested'." + " What is your next step?";

  public static final String MESSAGE_ABOUT_RULES_1 = "If you need the registration "
      + "instructions in Discord, just write you email here!\n\n"
      + "Before you start, be sure to familiarize "
      + "yourself with the rules. You can read the rules by clicking on this link.:point_down:";
  public static final String MESSAGE_ABOUT_RULES_2 =
      "https://github.com/Broscorp-net/traineeship/blob/master/README.md";
  public static final String MESSAGE_ABOUT_RULES_3 =
      "After you have read the rules," + " please answer a few questions.";
  public static final String MESSAGE_ABOUT_RULES_4 =
      "`Let me know when you are ready" + " (write 'ready').`";
  public static final String WRONG_ADDING_TO_ROLE =
      "Something went wrong when adding to role." + " You need to contact the admin!";
  public static final String ERROR_WITH_ADDING_GIT_NAME =
      "Something went wrong with adding to the team. Please, contact ";
  public static final String CONGRATS = "That was The End, Congrats";
  public static final String NO_ACTIVITY_MESSAGE = "There was no activity last week :pensive:";
  public static final String ESTIMATE_HEADER = "Please, rate the usefulness of the task for you:";
  public static final String ESTIMATE_QUESTION_FIRST = " *1* - not at all useful;";
  public static final String ESTIMATE_QUESTION_SECOND = " *2* - not very useful;";
  public static final String ESTIMATE_QUESTION_THIRD = " *3* - neutral;";
  public static final String ESTIMATE_QUESTION_FOURTH = " *4* - useful;";
  public static final String ESTIMATE_QUESTION_FIFTH = " *5* - very useful.";
  public static final String ESTIMATE_FOOTER = "Enter your rating:";
  public static final String CONFIRM_ESTIMATE = "Do you confirm this assessment (*yes*/*no*)?";
  public static final String CHOOSE_AN_ANSWER = "Choose an answer from 1 to 5.";
  public static final String RATING_MESSAGE = ":point_right: Рейтинг этой недели :point_left:";
  public static final String TASKS_STATUS_MESSAGE =
      ":point_right: Прогресс выполнения заданий :point_left:";

  // Information channels message
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_1 =
      "We have several channels to help you during your internship:";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_2 =
      "Please write"
          + " a few words about yourself) \n Yeees, perhaps you will work with these people"
          + " in one team :slightly_smiling_face: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_3 =
      "You can ask" + " questions on any subject and get an answer :nerd_face: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_4 =
      "Channel for"
          + " important messages and various announcements:rolled_up_newspaper::bookmark_tabs: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_5 =
      "A channel for" + " communication on any topics :yum: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_6 =
      "Stories" + " of those who have completed an internship and found a job :sunglasses: ";
  public static final String MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_7 =
      "`Please use the trades to" + " respond to messages.`";

  public static final String[] INFO_CHANNEL_MESSAGES = {
    MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_1,
    MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_2,
    MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_3,
    MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_4,
    MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_5,
    MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_6,
    MESSAGE_ABOUT_SEVERAL_INFO_CHANNEL_7
  };

  public static final String[] ESTIMATE_QUESTIONS = {
    ESTIMATE_QUESTION_FIRST,
    ESTIMATE_QUESTION_SECOND,
    ESTIMATE_QUESTION_THIRD,
    ESTIMATE_QUESTION_FOURTH,
    ESTIMATE_QUESTION_FIFTH
  };


  public static final String[] TASKS_FOR_USERS = {
      "intro", "generics", "game-of-life", "gc-implementation"
  };

  public static final Map<String, String> POINTS_FOR_TASK =
      Splitter.on(", ")
          .withKeyValueSeparator(":")
          .split(
              "checkstyle:1, primitives:2, boxing:2, valueref:3, "
                  + "equals/hashcode:3, platform:3, bytecode:2, gc:4,"
                      + " exceptions:4, classpath:3, generics:5, "
                  + "inner/classes:5, override/overload:4, strings:5, gamelife:10");

  public static final String EMAIL =  "<pre>Добрый день! Мы готовы начать нашу стажировку.\n"
          + "Пожалуйста, зарегистрируйтесь, в Дискорде (c пк): "
          + "<a href = \"https://discord.com/register\">Discord - A New Way to Chat with Friends & Communities</a>\n"
          + "После регистрации, присоединяйтесь в наше комьюнити: "
          + "<a href =\"https://discord.gg/7CJpUBefDD\">Broscorp-Community</a>\n"
          + "Ура, Вас поприветствует Bro_Bot - создан для того, чтобы помочь "
          + "Вам пройти первый этап нашей стажировки.\n"
          + "Внимательно ознакомьтесь с инструкцией для выполнения заданий. "
          + "После прочтения инструкции\n"
          + "дайте ответ на несколько вопросов, которые задаст наш Bro_Bot. "
          + "Следуйте дальнейшим указаниям нашего Bro!\n"
          + "Наш друг будет присылать линки на следующую таску после выполнения "
          + "предыдущей задачки (после пулл реквеста).\n"
          + "Пожалуйста, выполняйте задачки исключительно в порядке выдачи тасок! \n"
          + "Если вдруг Bro не выдает Вам задачу или у Вас появились "
          + "какие-то вопросы - напишите мне на почту \n"
          + "или в Discord (Dany Mruie#0342). Удачи!</pre>";

  public static final String WELCOME_MENTION =
      "Greetings, %s! Use /register and provide your GitHub username to complete registration";
  public static final String WELCOME_OLD_MENTION = "Greetings, %s! Haven't seen you in a while.";
  public static final String GITHUB_ACCOUNT_NOT_FOUND =
      "GitHub username you provided is incorrect. Check it and try again.";
  public static final String REGISTRATION_COMPLETED =
      "Registration completed! Now you should see more channels available.";
  public static final String USERNAME_UPDATED = "Username updated successfully!";
  public static final String NOT_REGISTERED =
      "You are not registered yet. Use /register and provide GitHub username";
  public static final String CURRENT_USERNAME = "Your GitHub username is \"%s\". "
      + "You can update it by calling /register and providing a username.";
}
