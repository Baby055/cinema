package school.hei.demo.conf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import school.hei.demo.service.UserService;

class UserServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private CurrentUserProvider currentUserProvider;
  private UserService subject;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    currentUserProvider = mock(CurrentUserProvider.class);
    subject = new UserService(userRepository, passwordEncoder, currentUserProvider);
  }

  @Test
  void anonymous_registration_is_always_created_as_client_even_if_manager_requested() {
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.empty());
    when(passwordEncoder.encode("secret")).thenReturn("hashed");
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveUser toSave = saveUserOf(null, "secret", UserRole.MANAGER);

    User created = subject.save(toSave);

    assertThat(created.getRole()).isEqualTo(UserRole.CLIENT);
    assertThat(created.getPassword()).isEqualTo("hashed");
    assertThat(created.getId()).isNotNull();
  }

  @Test
  void manager_can_create_an_employee_directly() {
    User manager = userWith(UserRole.MANAGER);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(manager)));
    when(passwordEncoder.encode(any())).thenReturn("hashed");
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveUser toSave = saveUserOf(null, "secret", UserRole.EMPLOYEE);

    User created = subject.save(toSave);

    assertThat(created.getRole()).isEqualTo(UserRole.EMPLOYEE);
  }

  @Test
  void manager_creating_a_user_without_a_role_defaults_to_client() {
    User manager = userWith(UserRole.MANAGER);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(manager)));
    when(passwordEncoder.encode(any())).thenReturn("hashed");
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveUser toSave = saveUserOf(null, "secret", null);

    User created = subject.save(toSave);

    assertThat(created.getRole()).isEqualTo(UserRole.CLIENT);
  }

  @Test
  void registration_without_a_password_throws_BadRequestException() {
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.empty());
    SaveUser toSave = saveUserOf(null, "   ", null);

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(BadRequestException.class);
  }

  @Test
  void update_without_authentication_throws_UnauthorizedException() {
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.empty());
    SaveUser toSave = saveUserOf(UUID.randomUUID(), null, null);

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(UnauthorizedException.class);
  }

  @Test
  void update_unknown_user_throws_NotFoundException() {
    User client = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(client)));
    UUID unknownId = UUID.randomUUID();
    when(userRepository.findById(unknownId)).thenReturn(Optional.empty());
    SaveUser toSave = saveUserOf(unknownId, null, null);

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void a_client_can_update_their_own_profile() {
    User client = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(client)));
    when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveUser toSave = profileUpdateOf(client.getId(), "Rindra", "0380000000");

    User updated = subject.save(toSave);

    assertThat(updated.getPhone()).isEqualTo("0380000000");
    verify(userRepository).save(client);
    verify(passwordEncoder, never()).encode(any());
  }

  @Test
  void a_client_cannot_update_someone_elses_profile() {
    User client = userWith(UserRole.CLIENT);
    User otherClient = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(client)));
    when(userRepository.findById(otherClient.getId())).thenReturn(Optional.of(otherClient));
    SaveUser toSave = saveUserOf(otherClient.getId(), null, null);

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(ForbiddenException.class);
  }

  @Test
  void a_manager_can_update_someone_elses_profile() {
    User manager = userWith(UserRole.MANAGER);
    User client = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(manager)));
    when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveUser toSave = saveUserOf(client.getId(), null, null);

    User updated = subject.save(toSave);

    assertThat(updated.getFirstName()).isEqualTo(toSave.firstName());
  }

  @Test
  void updating_the_password_re_hashes_it() {
    User client = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(client)));
    when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
    when(passwordEncoder.encode("newSecret")).thenReturn("newHashed");
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveUser toSave = saveUserOf(client.getId(), "newSecret", null);

    User updated = subject.save(toSave);

    assertThat(updated.getPassword()).isEqualTo("newHashed");
  }

  @Test
  void a_client_cannot_change_their_own_role() {
    User client = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(client)));
    when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
    SaveUser toSave = saveUserOf(client.getId(), null, UserRole.MANAGER);

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(ForbiddenException.class);
  }

  @Test
  void a_manager_can_change_someones_role() {
    User manager = userWith(UserRole.MANAGER);
    User client = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(manager)));
    when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveUser toSave = saveUserOf(client.getId(), null, UserRole.EMPLOYEE);

    User updated = subject.save(toSave);

    assertThat(updated.getRole()).isEqualTo(UserRole.EMPLOYEE);
  }

  @Test
  void findAll_returns_every_user() {
    User user = userWith(UserRole.CLIENT);
    when(userRepository.findAll()).thenReturn(List.of(user));

    assertThat(subject.findAll()).containsExactly(user);
  }

  @Test
  void findById_without_authentication_throws_UnauthorizedException() {
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.empty());

    assertThatThrownBy(() -> subject.findById(UUID.randomUUID()))
        .isInstanceOf(UnauthorizedException.class);
  }

  @Test
  void a_client_can_read_their_own_profile() {
    User client = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(client)));
    when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));

    assertThat(subject.findById(client.getId())).isEqualTo(client);
  }

  @Test
  void a_client_cannot_read_someone_elses_profile() {
    User client = userWith(UserRole.CLIENT);
    User otherClient = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(client)));
    when(userRepository.findById(otherClient.getId())).thenReturn(Optional.of(otherClient));

    assertThatThrownBy(() -> subject.findById(otherClient.getId()))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void a_manager_can_read_anyones_profile() {
    User manager = userWith(UserRole.MANAGER);
    User client = userWith(UserRole.CLIENT);
    when(currentUserProvider.getIfPresent()).thenReturn(Optional.of(asAuthenticatedUser(manager)));
    when(userRepository.findById(client.getId())).thenReturn(Optional.of(client));

    assertThat(subject.findById(client.getId())).isEqualTo(client);
  }

  private User userWith(UserRole role) {
    return new User(
        UUID.randomUUID(),
        "Rindra",
        "Rakoto",
        LocalDate.of(2000, 1, 1),
        "rindra-" + UUID.randomUUID() + "@hei.school",
        "hashed",
        "0340000000",
        role);
  }

  private SaveUser saveUserOf(UUID id, String password, UserRole role) {
    return new SaveUser(
        id,
        "Rindra",
        "Rakoto",
        LocalDate.of(2000, 1, 1),
        "rindra@hei.school",
        password,
        "0340000000",
        role);
  }

  private SaveUser profileUpdateOf(UUID id, String firstName, String phone) {
    return new SaveUser(
        id, firstName, "Rakoto", LocalDate.of(2000, 1, 1), "rindra@hei.school", null, phone, null);
  }

  private AuthenticatedUser asAuthenticatedUser(User user) {
    return new AuthenticatedUser(user);
  }
}
