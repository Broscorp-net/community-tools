package com.community.tools.service;


import com.community.tools.service.github.GitHubConnectService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CountingCompletedTasksService {

  private final GitHubConnectService service;


  public Map<String, List<String>> getCountedCompletedTasks() throws IOException {

    Map<String, List<GHPullRequest>> mapUsers1;
    Map<String, List<String>> mapUsers = new HashMap<>();

    mapUsers1 = service.getGitHubRepository()
        .getPullRequests(GHIssueState.CLOSED)
        .stream()
        .filter(pr -> {
          try {
            return pr.getLabels().stream().anyMatch(s -> s.getName().equals("done"));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.groupingBy(
            (GHPullRequest ghPullRequest) -> {
              try {
                return ghPullRequest.getUser().getLogin();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }));

    mapUsers1.forEach((key, value) -> mapUsers.put(key, new ArrayList<>()));
    mapUsers1.forEach((key, value) -> value.forEach(e -> mapUsers.get(key).add(e.getTitle())));

    return mapUsers;
  }
}
