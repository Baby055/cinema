package school.hei.demo.endpoint.rest.mapper;

import org.springframework.stereotype.Component;
import school.hei.demo.endpoint.rest.model.UserRest;
import school.hei.demo.model.User;

@Component
public class UserMapper {
    public UserRest toRest(User user) {
        return new UserRest(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}
