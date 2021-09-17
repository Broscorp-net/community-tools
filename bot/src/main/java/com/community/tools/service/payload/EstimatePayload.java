package com.community.tools.service.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EstimatePayload extends Payload {

  private Integer value;

  /**
   * Constructor for class.
   *
   * @param id    - usersId
   * @param value - value answer in estimate
   */
  public EstimatePayload(String id, Integer value) {
    super(id);
    this.value = value;
  }

}
