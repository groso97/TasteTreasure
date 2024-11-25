package com.project.TasteTreasure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.project.TasteTreasure.repositories.user.CustomUserDetailsService;

@Configuration
public class SecurityConfig {
        
     private final CustomUserDetailsService customUserDetailsService;

     @Autowired
     public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
             this.customUserDetailsService = customUserDetailsService;
     }

     @Autowired
     private PasswordEncoder passwordEncoder;

     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
             http
             .csrf((csrf -> csrf.disable()))
             .authorizeHttpRequests((auth) -> auth
                             .requestMatchers("/login", "/registration", "/processRegistration",
                                             "/confirm/**", "/", "/contact", "/search",
                                             "/css/**", "/img/**", "/js/**", "/error", "/account",
                                             "/owl-carousel-resources/**", "/recipes/**")
                             .permitAll()
                             .anyRequest().authenticated())
             .formLogin((formLogin) -> formLogin
                             .loginPage("/login")
                             .loginProcessingUrl("/login")
                             .failureHandler(databaseLoginFailureHandler)
                             .successHandler(databaseLoginSuccessHandler)
                             .permitAll())
             .logout((logout) -> logout
                             .logoutUrl("/logout")
                             .logoutSuccessUrl("/?logoutSuccess")
                             .invalidateHttpSession(true)
                             .permitAll())
             .httpBasic(Customizer.withDefaults());
             return http.build();
     }

     @Autowired
     public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
             auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
     }

     @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
             return authConfig.getAuthenticationManager();
     }

     @Autowired
     private DatabaseLoginFailureHandler databaseLoginFailureHandler;

     @Autowired
     private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
}