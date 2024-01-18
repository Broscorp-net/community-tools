package com.community.tools.service;

import com.community.tools.dto.UserForTaskStatusDto;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

public class TaskStatusServiceHooksImpl implements TaskStatusService {

  @Override
  public List<UserForTaskStatusDto> getTaskStatuses(Period period, Integer limit,
      Comparator<UserForTaskStatusDto> comparator) {
    return null;
  }
}
