package school.hei.demo.endpoint.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.hei.demo.endpoint.rest.mapper.UserMapper;
import school.hei.demo.endpoint.rest.model.SaveUser;
import school.hei.demo.endpoint.rest.model.UserRest;
import school.hei.demo.service.UserService;

@RestController
@AllArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping("/users")
  public List<UserRest> findAll() {
    return userService.findAll().stream().map(userMapper::toRest).toList();
  }

  @GetMapping("/users/{id}")
  public UserRest findById(@PathVariable UUID id) {
    return userMapper.toRest(userService.findById(id));
  }

  @PutMapping("/users")
  public UserRest save(@Valid @RequestBody SaveUser toSave) {
    return userMapper.toRest(userService.save(toSave));
  }
}
