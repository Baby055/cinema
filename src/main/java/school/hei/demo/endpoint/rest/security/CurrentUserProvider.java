package school.hei.demo.endpoint.rest.security;

import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import school.hei.demo.PojaGenerated;

@PojaGenerated
@Component
public class CurrentUserProvider {
  public AuthenticatedUser get() {
    return (AuthenticatedUser)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public Optional<AuthenticatedUser> getIfPresent() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser) {
      return Optional.of(authenticatedUser);
    }
    return Optional.empty();
  }
}
