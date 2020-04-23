package com.community.tools.util.statemachie;

import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
public class StateController {

  private final StateService stateService;

  public StateController(StateService purchseSevice) {
    this.stateService = purchseSevice;
  }

  @RequestMapping(path = "/reserve")
  public boolean reserve(final String userId, final String productId) {
    return stateService.reserved(userId, productId);
  }

  @RequestMapping(path = "/cancel")
  public boolean cancelReserve(final String userId) {
    return stateService.cancelReserve(userId);
  }

  @RequestMapping(path= "/buy")
  public  boolean buyReserve(final String userId){
    return stateService.buy(userId);
  }
}
