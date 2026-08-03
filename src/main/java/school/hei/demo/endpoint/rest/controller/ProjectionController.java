package school.hei.demo.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.hei.demo.endpoint.rest.mapper.ProjectionMapper;
import school.hei.demo.endpoint.rest.model.ProjectionRest;
import school.hei.demo.endpoint.rest.model.SaveProjection;
import school.hei.demo.service.ProjectionService;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ProjectionController {
    private final ProjectionService projectionService;
    private final ProjectionMapper projectionMapper;

    @GetMapping("/projections")
    public List<ProjectionRest> findAll(){
        return projectionService.findAll().stream().map(projectionMapper::toRest).toList();
    }

    @GetMapping("/projections/{id}")
    public ProjectionRest findById(@PathVariable UUID id){
        return projectionMapper.toRest(projectionService.findById(id));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/projection")
    public ProjectionRest save(@RequestBody SaveProjection toSave) {
        return projectionMapper.toRest(projectionService.save(toSave));
    }
}
