package com.community.tools.discord;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("discord")
public class DiscordConfig {

  @Value("${discord.token}")
  private String token;

  @Autowired
  private DiscordEventListener discordEventListener;

  /**
   * Created and configure object JDA.
   *
   * @return object JDA
   */
  @Bean
  public JDA jda() {
    try {
      JDA jda = JDABuilder.createDefault(token)
          .setChunkingFilter(ChunkingFilter.ALL)
          .setMemberCachePolicy(MemberCachePolicy.ALL)
          .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
          .enableIntents(GatewayIntent.GUILD_MEMBERS)
          .setBulkDeleteSplittingEnabled(false)
          .setCompression(Compression.NONE)
          .setActivity(Activity.playing("Discord"))
          .addEventListeners(discordEventListener)
          .build();
      jda.updateCommands()
              .addCommands(new CommandData("stat", "Показать статистику"))
              .queue();
      jda.awaitReady();
      return jda;
    } catch (LoginException | InterruptedException exception) {
      throw new RuntimeException(exception);
    }
  }

}
