package com.community.tools.discord;

import com.community.tools.service.EventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("discord")
@RequiredArgsConstructor
public class DiscordEventListener extends ListenerAdapter {

  private final EventListener listener;

  @Override
  public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
    listener.memberJoin(event);
  }

  @Override
  public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
    if (!event.getAuthor().isBot()) {
      listener.privateMessageReceived(event);
    }
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    super.onReady(event);
    log.info("{} is ready", event.getJDA().getSelfUser());
  }

  @Override
  public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    if (!event.getAuthor().isBot()) {
      listener.guildMessageReceived(event);
    }
  }

  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    listener.commandReceived(event);
  }
}
