package com.community.tools.model;

import java.util.Date;
import lombok.Data;
import org.kohsuke.github.GHReaction;
import org.kohsuke.github.PagedIterable;

@Data
public class GitHubComment {

  String authorComment;
  Date createdAt;
  PagedIterable<GHReaction> listOfReaction;
}
