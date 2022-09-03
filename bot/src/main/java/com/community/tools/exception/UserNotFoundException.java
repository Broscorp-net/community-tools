package com.community.tools.exception;

public class UserNotFoundException extends RuntimeException {

  private final String nameId;

  public UserNotFoundException(String nameId) {
    this.nameId = nameId;
  }

  @Override
  public String getMessage() {
    return "User with this name not found - " + nameId;
  }

  @Override
  public String toString() {
    return "UserNotFoundException";
  }

}
