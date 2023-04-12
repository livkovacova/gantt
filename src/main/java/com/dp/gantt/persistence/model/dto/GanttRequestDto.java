package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.Phase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GanttRequestDto {

    private Long projectId;

    private List<PhaseDto> phases;
}
