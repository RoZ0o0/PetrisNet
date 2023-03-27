package net.petri.springboot.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    AuthenticationManager authenticationManager;

    private final DataSource dataSource;
    private final ObjectMapper objectMapper;
    private final RestAuthenticationSuccessHandler successHandler;
    private final RestAuthenticationFailureHandler failureHandler;
    private final String secret;

    public SecurityConfig(DataSource dataSource, ObjectMapper objectMapper, RestAuthenticationSuccessHandler successHandler,
                          RestAuthenticationFailureHandler failureHandler, @Value("${jwt.secret}") String secret) {
        this.dataSource = dataSource;
        this.objectMapper = objectMapper;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.secret = secret;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImplementation();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService());
        authenticationManager = authenticationManagerBuilder.build();


        http.csrf().disable();
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/auth/login").permitAll()
                .antMatchers(HttpMethod.GET, "/api/saved_nets/ref/{\\w+}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/saved_nets/public").permitAll()
                .antMatchers(HttpMethod.GET, "/api/example_nets").permitAll()
                .antMatchers(HttpMethod.GET, "/api/example_nets/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/saved_nets/public/fetchAll").permitAll()
                .antMatchers(HttpMethod.GET, "/api/saved_nets/public/search").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/search").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/verify").permitAll()
                .antMatchers("/api/users/reset").permitAll()
                .antMatchers("/api/users/resetMail").permitAll()
                .antMatchers("/api/simulation").permitAll()
                .antMatchers("/api/simulation/check").permitAll()
                .antMatchers("/api/simulation/transition").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/user").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/api/auth/user/role").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.PUT, "/api/example_nets").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/example_nets").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/example_nets/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/saved_nets/ref").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/saved_nets").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/saved_nets/find").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/saved_nets/fetchAll").hasAnyRole("ADMIN")
                .antMatchers("/api/saved_nets/user").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/saved_nets/user/fetchAll").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/saved_nets/{\\d+}").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.PUT, "/api/saved_nets/public").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.PUT, "/api/users/profile").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/users").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/users/searchAll").hasAnyRole("ADMIN")
                .antMatchers("/api/users/email").hasAnyRole("ADMIN")
                .antMatchers("/api/users/fetchAll").hasAnyRole("ADMIN")
                .antMatchers("/api/users/{\\d+}").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(authenticationProvider())
                .authenticationManager(authenticationManager)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(authenticationFilter())
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userDetailsService(), secret))
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.cors().configurationSource(x-> {
            var cors = new CorsConfiguration();
            cors.addAllowedOriginPattern("*");
            cors.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT"));
            cors.setAllowedHeaders(List.of("*"));
            cors.setAllowCredentials(Boolean.TRUE);
            cors.setExposedHeaders(List.of("Authorization"));
            return cors;
        });

        return http.build();
    }

    public JsonObjectAuthenticationFilter authenticationFilter() throws Exception {
        JsonObjectAuthenticationFilter authenticationFilter = new JsonObjectAuthenticationFilter(objectMapper);
        authenticationFilter.setFilterProcessesUrl("/api/auth/login");
        authenticationFilter.setAuthenticationSuccessHandler(successHandler);
        authenticationFilter.setAuthenticationFailureHandler(failureHandler);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        return authenticationFilter;
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        return new JdbcUserDetailsManager(dataSource);
    }
}