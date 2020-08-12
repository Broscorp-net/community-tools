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

public class FirstAgreeLicenseAction implements Action<State,Event> {

  @Value("${firstAgreeMessage}")
  private String firstAgreeMessage;
  @Autowired
  private SlackService slackService;

  @Override
  public void execute(StateContext<State, Event> stateContext) {
    String user = stateContext.getExtendedState().getVariables().get("id").toString();
    firstAgreeMessage = "[{\"type\": \"header\",\"text\": {\"type\": \"plain_text\",\"text\": \"ОСНОВНЫЕ ПРАВИЛА\",\"emoji\": true}},{\"type\": \"divider\"},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \" 1.   Код форматируется в соответствии с *Google code style*. \\nНастройки для среды разработки (https://github.com/google/styleguide): \\n <https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml|IntelliJ IDEA> \\n <https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml|Eclipse>\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"  2.   Все проекты собираются с помощью *Maven*.\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"3. Файлы среды разработки и прочие временные файлы не должны попадать в репозиторий (https://github.com/github/gitignore).\"}]},{\"type\": \"context\",\"elements\": [{\"type\": \"mrkdwn\",\"text\": \"4.   Покрытие кода *Unit tests* (Junit5): \\n\\tТест проверяет один кусок логики за раз. То есть, если необходимо проверить как работает метод, который мы проверяем с правильными данными – это _один тест_. Если необходимо проверить как работает метод с другими данными - _второй тест_. \\n\\n *Тест пишется по принципу:* \\n\\t 1.  Подготовка тестовых данных. \\n\\t 2.  Исполнение метода, который мы тестируем. \\n\\t 3.  Проверка результата.\"}]}]";
    try {
      slackService.sendBlocksMessage(slackService.getUserById(user), firstAgreeMessage);
    }catch (JsonParseException e){
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }
}
