package com.dp.gantt.service;

import com.dp.gantt.model.PageResponse;
import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.RoleType;
import com.dp.gantt.persistence.model.dto.ProjectDto;
import com.dp.gantt.persistence.repository.ProjectRepository;
import com.dp.gantt.service.mapper.ProjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public List<ProjectDto> getUsersProjects(Long id, RoleType roleType){
        List<Project> result;
        if(roleType == RoleType.TEAM_MEMBER){
            result = projectRepository.findAllByMembers_id(id);
        }
        else{
            result = projectRepository.findAllByManager_id(id);
        }
        return projectMapper.projectListToProjectDtoList(result);
    }

    public PageResponse<ProjectDto> getUsersProjects(Long id, RoleType roleType, Integer page, Integer size, String orderBy, String direction){
        Sort sorting = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable paging = PageRequest.of(page, size, sorting);
        Page<Project> projectPage;
        if(roleType == RoleType.TEAM_MEMBER){
            projectPage = projectRepository.findAllByMembers_id(id, paging);
        }
        else{
            projectPage = projectRepository.findAllByManager_id(id, paging);
        }
//        if (name == null && date == null) {
//            projectPage = eventRepository.findAllBy(paging);
//        } else {
//            projectPage = getEventsPageWithFiltering(eventState, eventTitle, paging);
//        }
        List<ProjectDto> projectDtoList = projectMapper.projectListToProjectDtoList(projectPage.getContent());
        return new PageResponse<>(projectPage.getTotalElements(), projectPage.getTotalPages(), projectDtoList);
    }
}
