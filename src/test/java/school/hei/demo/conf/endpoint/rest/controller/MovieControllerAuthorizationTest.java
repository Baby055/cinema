package school.hei.demo.conf.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import school.hei.demo.endpoint.rest.controller.MovieController;
import school.hei.demo.endpoint.rest.mapper.MovieMapper;
import school.hei.demo.endpoint.rest.model.MovieRest;
import school.hei.demo.endpoint.rest.model.SaveMovie;
import school.hei.demo.endpoint.rest.security.AppUserDetailsService;
import school.hei.demo.endpoint.rest.security.SecurityConf;
import school.hei.demo.model.Genre;
import school.hei.demo.model.Movie;
import school.hei.demo.service.MovieService;

@WebMvcTest(MovieController.class)
@Import(SecurityConf.class)
public class MovieControllerAuthorizationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private MovieService movieService;
  @MockBean private MovieMapper movieMapper;
  @MockBean private AppUserDetailsService appUserDetailsService;

  private String requestBody() throws Exception {
    var toSave = new SaveMovie(null, "Interstellar", Set.of(Genre.SCI_FI), "A space odyssey", 169L);
    return objectMapper.writeValueAsString(toSave);
  }

  @Test
  @WithMockUser(roles = "CLIENT")
  void client_cannot_create_a_movie() throws Exception {
    mockMvc
        .perform(put("/movies").contentType(MediaType.APPLICATION_JSON).content(requestBody()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "EMPLOYEE")
  void employee_cannot_create_a_movie() throws Exception {
    mockMvc
        .perform(put("/movies").contentType(MediaType.APPLICATION_JSON).content(requestBody()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void manager_can_create_a_movie() throws Exception {
    Movie saved = new Movie();
    saved.setId(UUID.randomUUID());
    saved.setTitle("Interstellar");
    saved.setGenres(Set.of(Genre.SCI_FI));
    saved.setDescription("A space odyssey");
    saved.setDuration(Duration.ofMinutes(169));
    when(movieService.save(any())).thenReturn(saved);
    when(movieMapper.toRest(saved))
        .thenReturn(
            new MovieRest(
                saved.getId(), saved.getTitle(), saved.getGenres(), saved.getDescription(), 169));

    mockMvc
        .perform(put("/movies").contentType(MediaType.APPLICATION_JSON).content(requestBody()))
        .andExpect(status().isOk());
  }
}
