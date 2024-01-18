package com.community.tools.service;

import com.community.tools.dto.UserForTaskStatusDto;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

public interface TaskStatusService {

  /**
   * Service for sorting, limiting and creating DTO.
   *
   * @param limit      - limit of users for view.
   * @param period     - period of days fow view.
   * @param comparator - sort order (DESC, ASC).
   * @return - return list of DTO.
   */
  List<UserForTaskStatusDto> getTaskStatuses(Period period, Integer limit,
      Comparator<UserForTaskStatusDto> comparator);

}
