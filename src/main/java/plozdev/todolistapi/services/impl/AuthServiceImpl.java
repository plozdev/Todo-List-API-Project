package plozdev.todolistapi.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plozdev.todolistapi.dto.auth.AuthResponse;
import plozdev.todolistapi.dto.auth.LoginRequest;
import plozdev.todolistapi.dto.auth.RegisterRequest;
import plozdev.todolistapi.entities.User;
import plozdev.todolistapi.mapper.UserMapper;
import plozdev.todolistapi.repository.UserRepository;
import plozdev.todolistapi.security.JwtService;
import plozdev.todolistapi.services.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        User newUser = userMapper.toEntity(request);

        userRepository.save(newUser);

        String jwtToken = jwtService.generateToken(newUser);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

}
