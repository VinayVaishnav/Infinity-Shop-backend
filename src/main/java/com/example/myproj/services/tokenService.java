package com.example.myproj.services;

import com.example.myproj.models.RefreshToken;
import com.example.myproj.models.User;
import com.example.myproj.models.UserInfo;
import com.example.myproj.models.jwtToken;
import com.example.myproj.repositories.TokenRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class tokenService {

    @Autowired
    public TokenRepo tokenRepo;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${refresh-token.expiration}")
    private long refreshTokenExpiration;

    public RefreshToken generateRefreshToken(UserInfo user) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setIni(Instant.now());
        refreshToken.setExp(
                Instant.now().plusMillis(refreshTokenExpiration)
        );
        refreshToken.setRevoked(false);

        return tokenRepo.save(refreshToken);
    }

    public jwtToken generateJwtToken(User user) {
        String token = Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();

        jwtToken jwt = new jwtToken();
        jwt.setToken(token);
        return jwt;
    }
//
//    public boolean validateRefreshToken(String token) {
//        return tokenRepo.findByTokenAndRevokedFalse(token)
//                .filter(this::isRefreshTokenActive)
//                .isPresent();
//    }
//
//    public Optional<jwtToken> refreshAccessToken(String refreshTokenValue) {
//        return tokenRepo.findByTokenAndRevokedFalse(refreshTokenValue)
//                .filter(this::isRefreshTokenActive)
//                .map(RefreshToken::getUser)
//                .map(this::generateJwtToken);
//    }

    @Transactional(readOnly = true)
    public TokenAuthResult authenticate(String jwtToken, String refreshToken) {
        Optional<String> username = getUsernameFromJwt(jwtToken);
        if (username.isPresent()) {
            return TokenAuthResult.valid(username.get());
        }

        return tokenRepo.findByTokenAndRevokedFalse(refreshToken)
                .filter(this::isRefreshTokenActive)
                .map(rt -> {
                    User user = toUser(rt.getUser());
                    return TokenAuthResult.refreshed(user.getUsername(), generateJwtToken(user));
                })
                .orElse(TokenAuthResult.unauthorized());
    }

    public Optional<String> getUsernameFromJwt(String token) {
        return getJwtClaim(token, "username", String.class);
    }

    public Optional<String> getRoleFromJwt(String token) {
        return getJwtClaim(token, "role", String.class);
    }

    private <T> Optional<T> getJwtClaim(String token, String claimName, Class<T> claimType) {
        try {
            T claim = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(claimName, claimType);
            return Optional.ofNullable(claim);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean validateJwtToken(String token) {
        return getUsernameFromJwt(token).isPresent();
    }

    @Transactional
    public void revokeRefreshToken(String refreshTokenValue) {
        tokenRepo.findByToken(refreshTokenValue).ifPresent(rt -> {
            rt.setRevoked(true);
            tokenRepo.save(rt);
        });
    }

    public record TokenAuthResult(AuthStatus status, String username, jwtToken newJwtToken) {
        public enum AuthStatus {
            VALID, REFRESHED, UNAUTHORIZED
        }

        public static TokenAuthResult valid(String username) {
            return new TokenAuthResult(AuthStatus.VALID, username, null);
        }

        public static TokenAuthResult refreshed(String username, jwtToken newJwtToken) {
            return new TokenAuthResult(AuthStatus.REFRESHED, username, newJwtToken);
        }

        public static TokenAuthResult unauthorized() {
            return new TokenAuthResult(AuthStatus.UNAUTHORIZED, null, null);
        }
    }

    private boolean isRefreshTokenActive(RefreshToken refreshToken) {
        return !refreshToken.isRevoked()
                && refreshToken.getExp().isAfter(Instant.now());
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private static User toUser(UserInfo userInfo) {
        User user = new User();
        user.setId(userInfo.getUserId());
        user.setUsername(userInfo.getUserName());
        user.setRole(userInfo.getRole());
        return user;
    }
}
