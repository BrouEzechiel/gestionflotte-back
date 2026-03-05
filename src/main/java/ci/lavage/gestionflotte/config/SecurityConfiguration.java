package ci.lavage.gestionflotte.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Désactivé car on utilise JWT
                .authorizeHttpRequests(auth -> auth
                        // 1. On laisse l'accès libre à la page de connexion
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Seul l'ADMIN peut voir le Dashboard et les exports
                        .requestMatchers("/api/dashboard/**").hasRole("ADMINISTRATEUR")

                        // 3. Seul l'ADMIN peut modifier la flotte ou les chauffeurs
                        .requestMatchers("/api/vehicules/**", "/api/chauffeurs/**").hasRole("ADMINISTRATEUR")

                        // 4. Le CAISSIER et l'ADMIN peuvent gérer les versements
                        .requestMatchers("/api/versements/**").hasAnyRole("ADMINISTRATEUR", "CAISSIER")

                        // Tout le reste doit être authentifié
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Pas de session côté serveur (JWT)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}