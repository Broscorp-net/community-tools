package com.community.tools.discord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Getter
@NoArgsConstructor
public abstract class Command {

  private CommandData commandData;

  protected Command(CommandData commandData, OptionData... options) {
    this.commandData = commandData;
    this.commandData.addOptions(options);
  }

  public abstract void run(SlashCommandEvent command);

}
