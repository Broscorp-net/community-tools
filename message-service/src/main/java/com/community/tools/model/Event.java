package com.community.tools.model;

public enum Event {

  COMMIT("Commit"),
  COMMENT("Comment"),
  PULL_REQUEST_CREATED("Pull Request created"),
  PULL_REQUEST_CLOSED("Pull Request closed");

  private final String title;

  public String getTitle() {
    return title;
  }

  Event(String title) {
    this.title = title;
  }
}