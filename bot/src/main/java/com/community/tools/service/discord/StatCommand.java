package com.community.tools.service.discord;

import com.community.tools.discord.Command;
import com.community.tools.service.StatisticService;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.springframework.stereotype.Component;


@Component
public class StatCommand extends Command {

  private final StatisticService statisticService;

  public StatCommand(StatisticService statisticService) {
    super(new CommandData("stat", "Receive stats"));
    this.statisticService = statisticService;
  }

  /**
   * Gets statistics from {@link StatisticService} and send a result to user.
   *
   * @param command received event from Discord
   */
  public void run(SlashCommandEvent command) {
    Member member = command.getMember();
    if (member == null) {
      command.reply("This command can be executed only by admins and in the guild").queue();
      return;
    }
    List<Role> userRoles = member.getRoles();
    boolean isAdmin = userRoles.stream().anyMatch(role -> role.getName().equals("admin"));

    if (isAdmin) {
      command.reply("Статистика генерируется.Пожалуйста подождите...").queue();
      statisticService.createStatisticsForDiscord();
    } else {
      command.reply("У вас недостаточно прав для выполнения этой команды.").queue();
    }
  }

}
