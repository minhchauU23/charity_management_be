package dev.ptit.charitymanagement.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class JWTUtils {
    @Value("${access_token.key}")
    String ACCESS_TOKEN_SECRET_KEY;
    @Value("${access_token.expiry}")
    Long ACCESS_EXPIRATION_TIME_MS ;

    @Value("${refresh_token.key}")
    String REFRESH_TOKEN_SECRET_KEY;
    @Value("${refresh_token.expiry}")
    Long REFRESH_EXPIRATION_TIME_MS ;



    private SecretKey getAccessTokenSigningKey(){
        byte[] keyBytes = Base64.getDecoder().decode(ACCESS_TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getRefreshTokenSigningKey(){
        byte[] keyBytes = Base64.getDecoder().decode(REFRESH_TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateAccessToken(String token){
        try {
            Jwts.parser().verifyWith(getAccessTokenSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
//            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    public boolean validateRefreshToken(String token){
        try {
            Jwts.parser().verifyWith(getRefreshTokenSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    public String generateAccessToken(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(user.getUsername())
                .audience().add( authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .and()
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + ACCESS_EXPIRATION_TIME_MS))
                .signWith(getAccessTokenSigningKey(), Jwts.SIG.HS256)
                .compact();
    }


    public String generateRefreshToken(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(REFRESH_EXPIRATION_TIME_MS, ChronoUnit.MILLIS)))
                .signWith(getRefreshTokenSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public  Claims extractRefresh(String token){
        Jws<Claims> claims = Jwts.parser().verifyWith(getRefreshTokenSigningKey()).build().parseSignedClaims(token);
        return claims.getPayload();
    }
    public  Claims extractAccess(String token){
        Jws<Claims> claims = Jwts.parser().verifyWith(getAccessTokenSigningKey()).build().parseSignedClaims(token);
        return claims.getPayload();
    }

//    public Token generateToken(Authentication authentication){
//        String accessToken = generateAccessToken(authentication);
//        String refreshToken = generateRefreshToken(authentication);
//        return Token.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }



}
