package com.community.tools.discord;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class CommandHandler {

  private final Map<String, Object> namesCommandsMap;

  /**
   * Constructor, which gathers all commands from app context.
   * @param context Spring's application context
   */
  public CommandHandler(ApplicationContext context) {
    this.namesCommandsMap = context.getBeansWithAnnotation(Command.class).entrySet().stream()
        .filter(CommandHandler::hasRunnableMethod)
        .collect(Collectors.toMap(e -> getCommandAnnotation(e).name(), Entry::getValue));
  }

  /**
   * Returns list of {@link CommandData} based on map of commands.
   * @return list of {@link CommandData}
   */
  public List<CommandData> getCommandsData() {
    return namesCommandsMap.entrySet().stream()
        .map(CommandHandler::getCommandData)
        .collect(Collectors.toList());
  }

  /**
   * Invokes runnable method of a command, passing received event.
   * @param event command event, received from Discord
   */
  public void runCommand(SlashCommandEvent event) {
    String name = event.getName();
    Object command = namesCommandsMap.get(name);
    Method method = getRunnableMethod(command);
    try {
      method.invoke(command, event);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static CommandData getCommandData(Entry<String, Object> e) {
    Command command = getCommandAnnotation(e);
    CommandData commandData = new CommandData(command.name(), command.description());
    if (command.optionTypes().length == 0) {
      return commandData;
    }

    if (optionsFieldsLengthsMatch(command)) {
      for (int i = 0; i < command.options().length; i++) {
        OptionData optionData = new OptionData(command.optionTypes()[i], command.options()[i],
            command.optionsDescriptions()[i], command.optionsRequirements()[i]);
        commandData.addOptions(optionData);
      }
    } else {
      log.warn("Command with name = [{}] has options lengths mismatch",
          command.name());
    }
    return commandData;
  }

  private static boolean optionsFieldsLengthsMatch(Command command) {
    return command.options().length == command.optionsDescriptions().length
        && command.options().length == command.optionsRequirements().length
        && command.options().length == command.optionTypes().length;
  }

  private static boolean hasRunnableMethod(Entry<String, Object> e) {
    return Arrays.stream(e.getValue().getClass().getDeclaredMethods())
        .anyMatch(m -> m.getParameterCount() == 1
            && m.getParameterTypes()[0].equals(SlashCommandEvent.class));
  }

  private static Method getRunnableMethod(Object command) {
    Method[] methods = command.getClass().getDeclaredMethods();
    return Arrays.stream(methods)
        .filter(m -> m.getParameterCount() == 1
            && m.getParameterTypes()[0].equals(SlashCommandEvent.class))
        .findAny()
        .orElseThrow(() -> new RuntimeException("No suitable method found in command"));
  }

  private static Command getCommandAnnotation(Entry<String, Object> e) {
    return e.getValue().getClass().getDeclaredAnnotation(Command.class);
  }

}
