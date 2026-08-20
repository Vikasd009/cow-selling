package com.vikas.cowselling.config;

import com.vikas.cowselling.security.CustomUserDetailsService;
import com.vikas.cowselling.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig( CustomUserDetailsService userDetailsService,
                           PasswordEncoder passwordEncoder,
                           JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers( "/api/auth/**" ).permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"
                                ).permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/cows", "/api/cows/**").permitAll()
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/cows/**").hasRole("SELLER")
                                .requestMatchers(HttpMethod.PUT, "/api/cows/**").hasRole("SELLER")
                                .requestMatchers(HttpMethod.DELETE, "/api/cows/**").hasRole("SELLER")
                                .requestMatchers(HttpMethod.PATCH, "/api/cows/**").hasRole("SELLER")
                                .requestMatchers( "/api/admin/**" ).hasRole("ADMIN")
                                .requestMatchers( "/api/seller/**" ).hasRole("SELLER")
                                .requestMatchers("/api/test/seller").hasRole("SELLER")
                                .requestMatchers("/api/test/admin").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/cows/*/enquiries").authenticated()
                                .requestMatchers("/api/seller/**").hasRole("SELLER")
                                .requestMatchers("/api/enquiries/**").hasRole("SELLER")
                                .requestMatchers("/api/my-enquiries").authenticated()
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/cows/*/enquiries"
                                ).authenticated()

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/cows/*/reviews"
                                ).authenticated()

                                .requestMatchers(
                                        "/api/my-enquiries"
                                ).authenticated()

                                .requestMatchers(
                                        "/api/seller/enquiries"
                                ).hasRole("SELLER")

                                .requestMatchers(
                                        HttpMethod.PATCH,
                                        "/api/enquiries/**"
                                ).hasRole("SELLER")

                                .requestMatchers(
                                        "/api/favorites/**"
                                ).authenticated()

                                .requestMatchers(
                                        "/api/notifications/**"
                                ).authenticated()

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/sellers/*/reviews",
                                        "/api/sellers/*/rating"
                                ).permitAll()
                                .anyRequest() .authenticated())
                .authenticationProvider( authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration ) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
