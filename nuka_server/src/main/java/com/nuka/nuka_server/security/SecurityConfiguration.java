package com.nuka.nuka_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nuka.nuka_server.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST })
public class SecurityConfiguration {
    
	    private final CustomUserDetailsService userDetailsService;
	    private final JwtAuthorizationFilter jwtAuthorizationFilter;

	    public SecurityConfiguration(CustomUserDetailsService customUserDetailsService, JwtAuthorizationFilter jwtAuthorizationFilter) {
	        this.userDetailsService = customUserDetailsService;
	        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
	    }
	    
	    @Bean
	    public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	    }
		
	    @Bean
	    public AuthenticationManager authenticationManager(HttpSecurity http, NoOpPasswordEncoder passwordEncoder)
	            throws Exception {
	        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
	        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	        return authenticationManagerBuilder.build();
	    }


	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http.csrf().disable()
	        	.cors().and()
	                .authorizeRequests()
	                .antMatchers("/ws/**").permitAll()
	                .antMatchers("/noAuth/**").permitAll()
	                .antMatchers("/api/**").authenticated()
	                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	                .and().addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	    }

	    @SuppressWarnings("deprecation")
	    @Bean
	    public NoOpPasswordEncoder nonePasswordEncoder() {
	        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	    }

}