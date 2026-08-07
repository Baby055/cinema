package school.hei.demo.endpoint.rest.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConf {
  private final AppUserDetailsService userDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
    var provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
            .sessionManagement(
                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                    authorize ->
                            authorize
                                    .requestMatchers("/ping", "/health/**")
                                    .permitAll()
                                    .requestMatchers(HttpMethod.GET, "/projections/**")
                                    .permitAll()
                                    .requestMatchers(HttpMethod.GET, "/movies/**")
                                    .permitAll()
                                    .requestMatchers(HttpMethod.PUT, "/users")
                                    .permitAll()

                                    .requestMatchers(HttpMethod.PUT, "/movies")
                                    .hasRole("MANAGER")
                                    .requestMatchers(HttpMethod.PUT, "/projection")
                                    .hasRole("MANAGER")
                                    .requestMatchers(HttpMethod.GET, "/users")
                                    .hasRole("MANAGER")

                                    .requestMatchers(HttpMethod.GET, "/reservations")
                                    .hasAnyRole("EMPLOYEE", "MANAGER")

                                    .anyRequest()
                                    .authenticated())
            .httpBasic(basic -> {});
    return http.build();
  }
}