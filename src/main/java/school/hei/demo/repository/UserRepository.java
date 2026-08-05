package school.hei.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.demo.model.User;
import school.hei.demo.repository.model.JUser;

@Repository
@AllArgsConstructor
public class UserRepository {
  private JUserRepository jUserRepository;

  public List<User> findAll() {
    return jUserRepository.findAll().stream().map(UserRepository::toDomain).toList();
  }

  public Optional<User> findById(UUID id) {
    return jUserRepository.findById(id).map(UserRepository::toDomain);
  }

  public Optional<User> findByEmail(String email) {
    return jUserRepository.findByEmail(email).map(UserRepository::toDomain);
  }

  public User save(User user) {
    return toDomain(jUserRepository.save(toEntity(user)));
  }

  public static User toDomain(JUser entity) {
    return new User(
        entity.getId(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getBirthDate(),
        entity.getEmail(),
        entity.getPassword(),
        entity.getPhone(),
        entity.getRole());
  }

  public static JUser toEntity(User user) {
    return new JUser(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getBirthDate(),
        user.getEmail(),
        user.getPassword(),
        user.getPhone(),
        user.getRole());
  }
}
