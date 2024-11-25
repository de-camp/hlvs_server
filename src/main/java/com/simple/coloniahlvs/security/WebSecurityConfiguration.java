package com.simple.coloniahlvs.security;

import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.services.UserService;
import com.simple.coloniahlvs.services.implementations.JWTTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JWTTokenFilter filter;

    public WebSecurityConfiguration(PasswordEncoder passwordEncoder, UserService userService, JWTTokenFilter filter) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.filter = filter;
    }


    @Bean
    AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder managerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);


        managerBuilder.userDetailsService(identifier -> {
                    User user = userService.findByIdentifier(identifier);

                    if(user == null)
                        throw new UsernameNotFoundException("User: " + identifier + ", not found");

                    return user;
                })
                .passwordEncoder(passwordEncoder);

        return managerBuilder.build();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //Http login and cors disabled
        http.httpBasic(withDefaults()).csrf(csrf -> csrf.disable());

        //Route filter
        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/entrance/**").permitAll()
                        .anyRequest().authenticated()
        );

        //Statelessness
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //UnAunthorized handler
        http.exceptionHandling(handling -> handling.authenticationEntryPoint((req, res, ex) -> {
            res.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Auth fail!"
            );
        }));

        //JWT filter
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
