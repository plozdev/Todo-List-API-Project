package plozdev.todolistapi.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plozdev.todolistapi.dto.auth.AuthResponse;
import plozdev.todolistapi.dto.auth.LoginRequest;
import plozdev.todolistapi.dto.auth.RefreshTokenRequest;
import plozdev.todolistapi.dto.auth.RegisterRequest;
import plozdev.todolistapi.entities.RefreshToken;
import plozdev.todolistapi.entities.User;
import plozdev.todolistapi.exception.InvalidAuthenticationException;
import plozdev.todolistapi.exception.UserAlreadyExistsException;
import plozdev.todolistapi.mapper.UserMapper;
import plozdev.todolistapi.repository.RefreshTokenRepository;
import plozdev.todolistapi.repository.UserRepository;
import plozdev.todolistapi.security.JwtService;
import plozdev.todolistapi.services.AuthService;
import plozdev.todolistapi.exception.InvalidRefreshTokenException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTokenDuration;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists!");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return AuthResponse.builder().build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("There is no user registered" +
                " with that email address."));

                try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new InvalidAuthenticationException("Password is incorrect.");
        }


        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(createRefreshToken(user).getToken())
                .build();
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        refToken.setUser(user);
        refToken.setToken(UUID.randomUUID().toString());
        refToken.setExpiryDate(LocalDateTime.now().plusDays(refreshTokenDuration));
        refToken.setRevoked(false);

        return refreshTokenRepository.save(refToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenRepository.findByToken(request.getRefreshToken())
                .filter(token -> !token.getRevoked())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user);
                    RefreshToken newRefreshToken = createRefreshToken(user);
                    return AuthResponse.builder()
                            .token(newAccessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .build();
                })
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired!"));
    }

    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresentOrElse(
                        token -> {
                            token.setRevoked(true);
                            refreshTokenRepository.save(token);
                        },
                        () -> {
                            throw new InvalidRefreshTokenException("Refresh token not found!");
                        }
                );
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new InvalidRefreshTokenException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
