package school.hei.demo.endpoint.rest.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import school.hei.demo.PojaGenerated;

@PojaGenerated
@Component
public class CurrentUserProvider {
    public AuthenticatedUser get(){
        return (AuthenticatedUser)  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
