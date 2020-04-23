package com.community.tools.util.statemachie;

public interface StateService {

  public boolean reserved(String userId, String productId) ;

  public boolean cancelReserve(String userId) ;

  public boolean buy(String userId) ;
}
