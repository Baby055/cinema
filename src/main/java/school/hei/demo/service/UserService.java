package school.hei.demo.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.hei.demo.endpoint.rest.exception.BadRequestException;
import school.hei.demo.endpoint.rest.exception.ForbiddenException;
import school.hei.demo.endpoint.rest.exception.NotFoundException;
import school.hei.demo.endpoint.rest.exception.UnauthorizedException;
import school.hei.demo.endpoint.rest.model.SaveUser;
import school.hei.demo.endpoint.rest.security.AuthenticatedUser;
import school.hei.demo.endpoint.rest.security.CurrentUserProvider;
import school.hei.demo.model.User;
import school.hei.demo.model.UserRole;
import school.hei.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public User save(SaveUser toSave){
        Optional<AuthenticatedUser> currentUser = currentUserProvider.getIfPresent();
        return toSave.id() == null ? create(toSave, currentUser) : update(toSave, currentUser);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        AuthenticatedUser requester =
                currentUserProvider
                        .getIfPresent()
                        .orElseThrow(
                                () -> new UnauthorizedException("You must be authenticated to view a user")
                        );
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new NotFoundException("User " + id + " not found"));

        boolean isSelf = requester.getId().equals(user.getId());
        boolean isManager = requester.getRole() == UserRole.MANAGER;
        if (!isSelf && !isManager) {
            throw new ForbiddenException("You can only view your own profile");
        }
        return user;
    }

    private User create(SaveUser toSave, Optional<AuthenticatedUser> currentUser){
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName(toSave.firstName());
        user.setLastName(toSave.lastName());
        user.setBirthDate(toSave.birthdate());
        user.setEmail(toSave.email());
        user.setPassword(hash(toSave.password()));
        user.setPhone(toSave.phone());
        user.setRole(resolveRoleForCreation(toSave.role(), currentUser));
        return userRepository.save(user);
    }

    private UserRole resolveRoleForCreation(UserRole requested, Optional<AuthenticatedUser> currentUser){
        boolean requesterIsManager = currentUser.map(u -> u.getRole() == UserRole.MANAGER).orElse(false);
        if (!requesterIsManager){
            return UserRole.CLIENT;
        }
        return requested == null ? UserRole.CLIENT : requested;
    }

    private User update(SaveUser toSave, Optional<AuthenticatedUser> currentUser){
        AuthenticatedUser requester =
                currentUser.orElseThrow(
                        () -> new UnauthorizedException("You must be authenticated to update a user")
                );
        User existing =
                userRepository
                        .findById(toSave.id())
                        .orElseThrow(
                                () -> new NotFoundException("User " + toSave.id() + " not found")
                        );

        boolean isSelf = requester.getId().equals(existing.getId());
        boolean isManager = requester.getRole() == UserRole.MANAGER;
        if (!isSelf && !isManager){
            throw new ForbiddenException("You can only update your own profile");
        }

        existing.setFirstName(toSave.firstName());
        existing.setLastName(toSave.lastName());
        existing.setBirthDate(toSave.birthdate());
        existing.setEmail(toSave.email());
        existing.setPhone(toSave.phone());
        if (toSave.password() != null && !toSave.password().isBlank()){
            existing.setPassword(passwordEncoder.encode(toSave.password()));
        }
        applyRoleChange(existing, toSave.role(), isManager);
        return userRepository.save(existing);
    }

    private void applyRoleChange(User existing, UserRole requestedRole, boolean requesterIsManager){
        if (requestedRole == null || requestedRole == existing.getRole()){
            return;
        }
        if (!requesterIsManager){
            throw new ForbiddenException("Only a manager can change a user's role");
        }
        existing.setRole(requestedRole);
    }

    private String hash(String rawPassword){
        if (rawPassword == null || rawPassword.isBlank()){
            throw new BadRequestException("password is required");
        }
        return passwordEncoder.encode(rawPassword);
    }
}
