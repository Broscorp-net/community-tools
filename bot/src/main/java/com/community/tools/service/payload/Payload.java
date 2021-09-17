package com.community.tools.service.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class Payload {

  protected String id;

  public Payload(String id) {
    this.id = id;
  }

}
