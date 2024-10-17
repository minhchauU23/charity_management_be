package dev.ptit.charitymanagement.filters;

import dev.ptit.charitymanagement.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearer = request.getHeader("Authorization");
        if(bearer != null && bearer.length() > 7){
            String token = bearer.substring(6);
            if(jwtUtils.validate(token) && !jwtUtils.isExpired()){
                Claims claims = jwtUtils.extract(token);
                Authentication authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), "",
                        claims
                                .getAudience()
                                .stream().map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet()));
                authentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
