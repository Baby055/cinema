package school.hei.demo.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.hei.demo.endpoint.rest.mapper.MovieMapper;
import school.hei.demo.endpoint.rest.model.MovieRest;
import school.hei.demo.endpoint.rest.model.SaveMovie;
import school.hei.demo.service.MovieService;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @GetMapping("/movies")
    public List<MovieRest> findAll(){
        return movieService.findAll().stream().map(movieMapper::toRest).toList();
    }

    @GetMapping("/movies/{id}")
    public MovieRest findById(@PathVariable UUID id){
        return  movieMapper.toRest(movieService.findById(id));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/movies")
    public MovieRest save(@RequestBody SaveMovie toSave){
        return movieMapper.toRest(movieService.save(toSave));
    }
}
