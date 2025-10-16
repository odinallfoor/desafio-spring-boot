package com.nuevospa.taskmanagement.infrastructure.web;

import com.nuevospa.taskmanagement.application.dto.auth.LoginRequestDTO;
import com.nuevospa.taskmanagement.application.dto.auth.TokenResponseDTO;
import com.nuevospa.taskmanagement.domain.model.user.User;
import com.nuevospa.taskmanagement.infrastructure.config.jwt.JwtProvider;
import com.nuevospa.taskmanagement.infrastructure.persistence.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;


    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    // Endpoint 1: Registro de un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody LoginRequestDTO request){
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ResponseEntity<>("Nombre de usuario ya existe.", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .build();

        userRepository.save(user);
        return new ResponseEntity<>("Usuario registrado con exito.", HttpStatus.CREATED);
    }

    // Endpoint 2: Inicio de sesión y generación de token
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO request) {
        // 1. Autenticar usando el AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // 2. Si la autenticación es exitosa, generar el token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtProvider.generateToken(userDetails);

        // 3. Devolver el token al cliente
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}
