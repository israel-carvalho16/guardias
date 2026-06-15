package com.project.omni.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig{
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
		  .csrf(csrf -> csrf.disable())
		  .authorizeHttpRequests(auth -> auth
				  .requestMatchers(
						  "/login",
						  "/cadastro",
						  "/salvar",
					      "/css/**",
					      "/js/**"
						  ).permitAll()
				       .anyRequest().authenticated()
				  )
		  .formLogin(form -> form
				  .loginPage("/login")
				  .defaultSuccessUrl("/home",true)
				  .permitAll()
				  )
		  .logout(logout -> logout
				  .logoutSuccessUrl("/login")
				  .permitAll()
				  );
		return http.build();
	}
}
