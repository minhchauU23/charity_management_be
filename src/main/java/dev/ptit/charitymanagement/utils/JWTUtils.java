package dev.ptit.charitymanagement.utils;

import dev.ptit.charitymanagement.dtos.response.auth.Token;
import dev.ptit.charitymanagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtils {
    private static final String SECRET_KEY = "zTxxp/VBTjqGSacbJsJ7YALhck7fFv/UHJvaGbR/TcnxUwDjuoN3FSS3CS/g2Pr2";
    private static final Long ACCESS_EXPIRATION_TIME_MS = 1800000L;
    private static final Long REFRESH_EXPIRATION_TIME_MS = 2592000000L;



    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validate(String token){
        return  false;
    }

    public boolean isExpired(){
        return false;
    }

    private String generateAccessToken(Authentication authentication){
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(user.getUsername())
                .audience().add( authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .and()
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + ACCESS_EXPIRATION_TIME_MS))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }


    private String generateRefreshToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + REFRESH_EXPIRATION_TIME_MS))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public  Claims extract(String token){
        Jws<Claims> claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
        return claims.getPayload();

    }

    public Token generateToken(Authentication authentication){
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);
        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }



}
