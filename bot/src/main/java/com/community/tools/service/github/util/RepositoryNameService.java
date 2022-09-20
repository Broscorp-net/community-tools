package com.community.tools.service.github.util;

import com.community.tools.service.github.util.dto.ParsedRepositoryName;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RepositoryNameService {

  private final Set<String> taskRepositoryNamesPrefixes;

  @Autowired
  public RepositoryNameService(@Value("${github.task-repository-name.prefixes}")
      String[] taskRepositoryNamesPrefixes) {
    this.taskRepositoryNamesPrefixes = new HashSet<>(Arrays.asList(taskRepositoryNamesPrefixes));
  }

  /**
   * Checks repository name to be prefixed with one of a given tasks name.
   *
   * @param repositoryName repository name
   * @return true if repository name is prefixed with task name or false otherwise
   */
  public boolean isPrefixedWithTaskName(String repositoryName) {
    return taskRepositoryNamesPrefixes
        .stream()
        .anyMatch(prefix -> repositoryName.startsWith(prefix + "-"));
  }

  /**
   * Splits repository name into task name and name of the repository creator.
   *
   * @param repositoryName repository name
   * @return ParsedRepositoryName object which contains task name and name of the repository creator
   */
  public ParsedRepositoryName parseRepositoryName(String repositoryName) {
    for (String prefix : taskRepositoryNamesPrefixes) {
      if (repositoryName.startsWith(prefix)) {
        String creatorGitName = repositoryName.replace(prefix + "-", "");
        return new ParsedRepositoryName(creatorGitName, prefix);
      }
    }

    throw new IllegalArgumentException("Repository name is not prefixed with valid task name");
  }

}
