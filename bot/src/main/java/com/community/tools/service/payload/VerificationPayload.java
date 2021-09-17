package com.community.tools.service.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerificationPayload extends Payload {

  private String gitNick;

  /**
   * Constructor for class.
   *
   * @param id      - usersId
   * @param gitNick - nick in Git
   */
  public VerificationPayload(String id, String gitNick) {
    super(id);
    this.gitNick = gitNick;
  }
}
