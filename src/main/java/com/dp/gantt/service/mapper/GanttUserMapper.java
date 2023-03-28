package com.dp.gantt.service.mapper;

import com.dp.gantt.persistence.model.GanttUser;
import com.dp.gantt.persistence.model.dto.GanttUserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GanttUserMapper {

    List<GanttUserDto> ganttUserListToGanttUserDtoList(List<GanttUser> ganttUser);
}
