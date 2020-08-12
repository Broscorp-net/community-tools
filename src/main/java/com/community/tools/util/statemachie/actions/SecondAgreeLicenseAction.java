package com.community.tools.util.statemachie.actions;

import com.community.tools.service.slack.SlackService;
import com.community.tools.util.statemachie.Event;
import com.community.tools.util.statemachie.State;
import com.github.seratch.jslack.api.methods.SlackApiException;

import java.io.IOException;

import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class SecondAgreeLicenseAction implements Action<State, Event> {

  @Value("${secondAgreeMessage}")
  private String secondAgreeMessage;
  @Autowired
  private SlackService slackService;

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    secondAgreeMessage ="[\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"header\",\n" +
            "\t\t\t\"text\": {\n" +
            "\t\t\t\t\"type\": \"plain_text\",\n" +
            "\t\t\t\t\"text\": \"ПРАВИЛА ВЫПОЛНЕНИЯ ЗАДАНИЯ\",\n" +
            "\t\t\t\t\"emoji\": true\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"divider\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"Первое, что тебе нужно сделать - *Fork* репозиторий. Если ты форкнул репозиторий не только что, а уже давно работаешь над заданиями – тебе нужно обновиться. <thttps://help.github.com/en/github/collaborating-with-issues-and-pull-requests/merging-an-upstream-repository-into-your-fork|Инструкция как это сделать.> \\n Если ты дисциплинированно создавал ветки и не добавлял коммиты в master то все должно пройти без конфликтов =)\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"header\",\n" +
            "\t\t\t\"text\": {\n" +
            "\t\t\t\t\"type\": \"plain_text\",\n" +
            "\t\t\t\t\"text\": \"Каждое новое задание необходимо выполнять по следующему алгоритму:\",\n" +
            "\t\t\t\t\"emoji\": true\n" +
            "\t\t\t}\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"1.    Каждое задание – *отдельная ветка*. Сделай ветку для задания и выполняй его в соответствии с инструкциями. Имя ветки должно *совпадать* с именем пакета задания.\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"2.    Сделай *pull request* из этой ветки в наш репозиторий. Имя pull request должно *совпадать* с именем пакета задания.\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"Мы проверим задание, подскажем что нужно исправить и примем задание. Если что-то надо поменять мы добавим метку *changes requested*.\\n\\nЕсли это произошло: \\n\\t1.   Исправь, пожалуйста, замечания. \\n\\t2.   Удали метку *changes requested* и добавь метку *ready for review*. \\n\\t3.   Метка *done* – задание принято.\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\": \"context\",\n" +
            "\t\t\t\"elements\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"type\": \"mrkdwn\",\n" +
            "\t\t\t\t\t\"text\": \"```Если ты не можешь менять метки – значит мы провтыкали и не добавили тебя в команду (или добавили, но приглашение не было принято, так что проверь почту) – напиши об этом Liliya Stepanovna в слеке.```\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t}\n" +
            "\t]";
    try {
      slackService.sendBlocksMessage(slackService.getUserById(user), secondAgreeMessage);
    }catch (JsonParseException e){
    }
    catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
