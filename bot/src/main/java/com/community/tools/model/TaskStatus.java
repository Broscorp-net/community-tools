package com.community.tools.model;

public enum TaskStatus {

  UNDEFINED("undefined"),
  NEW("new"),
  PULL_REQUEST("pull request"),
  READY_FOR_REVIEW("ready for review"),
  CHANGES_REQUESTED("changes requested"),
  FAILURE("failure"),
  DONE("done");

  private final String description;

  TaskStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

}
