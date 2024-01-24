package com.community.tools.service.discord;

import com.community.tools.discord.Command;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Component
public class RegisterCommand extends Command {

  private static final String OPTION_NAME = "username";
  private final StateMachineService stateMachineService;

  protected RegisterCommand(StateMachineService stateMachineService) {
    super(new CommandData("register", "Saves your GitHub username"),
        new OptionData(OptionType.STRING, OPTION_NAME, "Your GitHub username", true));
    this.stateMachineService = stateMachineService;
  }

  @Override
  public void run(SlashCommandEvent command) {
    String userId = command.getUser().getId();
    StateMachine<State, Event> machine = getStateMachine(userId);
    String content;
    if (machine.getState().getId() != State.NEW_USER) {
      content = "You are already registered!";
    } else if (command.isFromGuild()) {
      content = "Searching... Check your private messages";
    } else {
      content = "Searching...";
    }
    command.reply(content).queue();
    String username = command.getOptionsByName(OPTION_NAME).get(0).getAsString();
    VerificationPayload payload = new VerificationPayload(userId, username);
    stateMachineService.doAction(machine, payload, Event.LOGIN_CONFIRMATION);
  }

  private StateMachine<State, Event> getStateMachine(String userId) {
    StateMachine<State, Event> machine;
    try {
      machine = stateMachineService.restoreMachine(userId);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return machine;
  }

}
