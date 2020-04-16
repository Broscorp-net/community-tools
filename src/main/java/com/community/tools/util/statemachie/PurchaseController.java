package com.community.tools.util.statemachie;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PurchaseController {

  private final PurchaseService purchaseService;

  public PurchaseController(PurchaseService purchseSevice) {
    this.purchaseService = purchseSevice;
  }

  @RequestMapping(path = "/reserve")
  public boolean reserve(final String userId, final String productId) {
    return purchaseService.reserved(userId, productId);
  }

  @RequestMapping(path = "/cancel")
  public boolean cancelReserve(final String userId) {
    return purchaseService.cancelReserve(userId);
  }

  @RequestMapping(path= "/buy")
  public  boolean buyReserve(final String userId){
    return purchaseService.buy(userId);
  }
}
