package com.community.tools.model;

import lombok.Data;
import net.dv8tion.jda.api.entities.User;

@Data
public class ServiceUser {

  private String id;
  private String name;

  /**
   * Convert an DiscordUser object into an ServiceUser object.
   *
   * @param discordUser - DiscordUser object.
   * @return a new ServiceUser object.
   */
  public static ServiceUser from(User discordUser) {
    ServiceUser user = new ServiceUser();

    user.setId(discordUser.getId());
    user.setName(discordUser.getName());

    return user;
  }

  /**
   * Convert an SlackUser object into an ServiceUser object.
   * @param slackUser - SlackUser object.
   * @return a new ServiceUser object.
   */
  public static ServiceUser from(com.github.seratch.jslack.api.model.User slackUser) {
    ServiceUser user = new ServiceUser();

    user.setId(slackUser.getId());
    user.setName(slackUser.getProfile().getDisplayName());

    return user;
  }
}
