package com.community.tools.controller;

import static com.community.tools.util.GetServerAddress.getAddress;

import com.community.tools.dto.UserTasksStatusDto;
import com.community.tools.model.User;
import com.community.tools.service.PullRequestsService;
import com.community.tools.service.TaskStatusService;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.kohsuke.github.GHPullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/taskstatus")
public class TasksStatusController {

  @Autowired
  private PullRequestsService pullRequestsService;

  @Autowired
  private TaskStatusService taskStatusService;

  @Autowired
  private StateMachineRepository stateMachineRepository;

  @Value("${tasksForUsers}")
  private String[] tasksForUsers;

  /**
   * This method return webpage with table of status.
   * @param model Model
   * @return webpage with template "tasksstatus"
   */
  @GetMapping
  public String getTaskStatus(Model model,
      @RequestParam(name = "sortByField", required = false, defaultValue = "gitName")
          String sortByField,
      @RequestParam(name = "sortDirection", required = false, defaultValue = "asc")
          String sortDirection) {

    List<UserTasksStatusDto> userTasksStatusDtoList = new ArrayList<>();

    List<User> users = taskStatusService
        .addPlatformNameToUser(1, sortByField, sortDirection);
    users.forEach(user -> {
      userTasksStatusDtoList.add(UserTasksStatusDto.fromUser(user, tasksForUsers));
    });

    String baseUrl = taskStatusService.getCurrentBaseUrl();
    model.addAttribute("tasksForUsers", tasksForUsers);
    model.addAttribute("userTasksStatuses", userTasksStatusDtoList);
    model.addAttribute("sortDirection", sortDirection);
    String reverseSortDirection = sortDirection.equals("asc") ? "desc" : "asc";
    model.addAttribute("reverseSortDirection", reverseSortDirection);
    model.addAttribute("baseUrl", baseUrl);

    return "tasksstatus";
  }

  /**
   * Page with confirmation.
   * @return page confirmation
   */
  @RequestMapping(value = "/download", method = RequestMethod.GET)
  public String confirmPage() {
    return "tasksstatus_download";
  }

  /**
   * Download and save into data base all tasks status.
   * @param response response
   */
  @RequestMapping(value = "/download", method = RequestMethod.POST)
  public String downloadAllTasksStatus(HttpServletResponse response) {
    List<GHPullRequest> ghPullRequests = pullRequestsService.getPullRequests();
    taskStatusService.cleanBootTasksStatus(ghPullRequests);
    return "redirect:";
  }

  /**
   * This method return image with table, which contains first 5 trainees of leaderboard.
   * @param response HttpServletResponse
   * @throws EntityNotFoundException EntityNotFoundException
   * @throws IOException IOException
   */
  @RequestMapping(value = "/img/{date}", method = RequestMethod.GET)
  public void getImage(HttpServletResponse response) throws EntityNotFoundException, IOException {
    String url = getAddress();
    byte[] data = taskStatusService.createImage(url);
    response.setContentType(MediaType.IMAGE_PNG_VALUE);
    response.getOutputStream().write(data);
    response.setContentLength(data.length);
  }

}
