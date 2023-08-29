package com.community.tools.discord;

import com.community.tools.model.Message;
import com.community.tools.service.EventListener;
import com.community.tools.service.StatisticService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("discord")
public class DiscordEventListener extends ListenerAdapter {

  @Autowired
  private EventListener listener;

  @Autowired
  private StatisticService statisticService;

  @Override
  public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
    String userId = event.getUser().getId();
    listener.memberJoin(new Message(userId, ""));
  }

  @Override
  public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
    if (!event.getAuthor().isBot()) {
      String messageFromUser = event.getMessage().getContentRaw();
      String userId = event.getAuthor().getId();
      Message message = new Message(userId, messageFromUser);
      listener.messageReceived(message);
    }
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    super.onReady(event);
    log.info("{} is ready", event.getJDA().getSelfUser());
  }

  @Override
  public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    if (event.getMessage().getChannel().toString().indexOf("welcome") != -1) {
      String userId = event.getAuthor().getId();
      Message message = new Message(userId, "welcome channel");
      listener.messageReceived(message);
    }
  }

  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    if ("statistic".equals(event.getName())) {
      List<Role> userRoles = event.getMember().getRoles();
      boolean isAdmin = userRoles.stream().anyMatch(role -> role.getName().equals("admin"));

      if (isAdmin) {
        event.reply("Статистика генерируется.Пожалуйста подождите...").queue();
        statisticService.createStatisticsForDiscord();
      } else {
        event.reply("У вас недостаточно прав для выполнения этой команды.").queue();
      }
    }
  }
}