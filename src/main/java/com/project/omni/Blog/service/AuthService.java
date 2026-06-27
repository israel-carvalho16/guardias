    package com.project.omni.Blog.service;

    import  com.project.omni.Blog.dto.request.LoginRequest;
    import  com.project.omni.Blog.dto.request.RegisterRequest;
    import  com.project.omni.Blog.dto.response.AuthResponse;
    import  com.project.omni.Blog.exception.ResourceNotFoundException;
    import  com.project.omni.Blog.model.Role;
    import  com.project.omni.Blog.model.User;
    import  com.project.omni.Blog.repository.RoleRepository;
    import  com.project.omni.Blog.repository.UserRepository;
    import  com.project.omni.Blog.security.jwt.JwtService;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.Set;

    @Service
    public class AuthService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final UserDetailsService userDetailsService;

        public AuthService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        AuthenticationManager authenticationManager,
                        UserDetailsService userDetailsService) {
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;
            this.passwordEncoder = passwordEncoder;
            this.jwtService = jwtService;
            this.authenticationManager = authenticationManager;
            this.userDetailsService = userDetailsService;
        }

        public AuthResponse register(RegisterRequest request) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email já cadastrado");
            }

            Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role USER não encontrada"));

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(Set.of(userRole));

            userRepository.save(user);

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtService.generateToken(userDetails);

            return new AuthResponse(token, user.getName(), user.getEmail(), "ROLE_USER");
        }

        public AuthResponse login(LoginRequest request) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtService.generateToken(userDetails);

            String role = user.getRoles().stream()
                    .findFirst()
                    .map(r -> r.getName().name())
                    .orElse("ROLE_USER");

            return new AuthResponse(token, user.getName(), user.getEmail(), role);
        }
    }