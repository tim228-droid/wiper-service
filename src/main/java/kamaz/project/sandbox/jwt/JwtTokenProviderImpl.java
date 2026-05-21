package kamaz.project.sandbox.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kamaz.project.sandbox.enums.TokenType;
import kamaz.project.sandbox.models.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.*;

@Service
public class JwtTokenProviderImpl implements JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getKey() {
        byte[] decoded = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(decoded);
    }

    @Override
    public Token generateAccessToken(Map<String, Object> extraClaims, long duration, TemporalUnit durationType, UserDetails user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plus(duration, durationType);
        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(toDate(now))
                .expiration(toDate(expiry))
                .signWith(getKey())
                .compact();
        return new Token(TokenType.ACCESS, token, expiry, false, null);
    }

    @Override
    public Token generateRefreshToken(long duration, TemporalUnit durationType, UserDetails user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plus(duration, durationType);
        String token = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(toDate(now))
                .expiration(toDate(expiry))
                .signWith(getKey())
                .compact();
        return new Token(TokenType.REFRESH, token, expiry, false, null);
    }

    @Override
    public boolean validateToken(String tokenValue) {
        if (tokenValue == null) return false;
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(tokenValue);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String tokenValue) {
        return Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(tokenValue).getPayload().getSubject();
    }

    @Override
    public LocalDateTime getExpiryDateFromToken(String tokenValue) {
        Date exp = Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(tokenValue).getPayload().getExpiration();
        return toLocalDateTime(exp);
    }

    private Date toDate(LocalDateTime ldt) { return Date.from(ldt.toInstant(ZoneOffset.UTC)); }
    private LocalDateTime toLocalDateTime(Date date) { return date.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime(); }
}