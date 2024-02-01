package com.community.tools.service;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public interface EventListener {

  void memberJoin(GuildMemberJoinEvent message);

  void commandReceived(SlashCommandEvent event);

  void guildMessageReceived(GuildMessageReceivedEvent event);

  void privateMessageReceived(PrivateMessageReceivedEvent event);
}
