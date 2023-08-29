package com.community.tools.model;

public enum TaskStatus {

  UNDEFINED("undefined", ":white_large_square:"),
  NEW("new", ":yellow_square:"),
  READY_FOR_REVIEW("ready for review", ":yellow_square:"),
  CHANGES_REQUESTED("changes requested", ":red_square:"),
  FAILURE("failure", ":red_square:"),
  DONE("done", ":white_check_mark:");

  private final String description;
  private final String emoji;

  TaskStatus(String description, String emoji) {
    this.description = description;
    this.emoji = emoji;
  }

  public String getDescription() {
    return description;
  }

  public String getEmoji() {
    return emoji;
  }

  /**
   * Retrieves the emoji associated with a given description of a task status.
   *
   * @param description The description of the task status.
   * @return The emoji corresponding to the description,
   *         or null if no matching emoji is found.
   */
  public static String getEmojiByDescription(String description) {
    for (TaskStatus status : TaskStatus.values()) {
      if (status.getDescription().equals(description)) {
        return status.getEmoji();
      }
    }
    return null;
  }

}
