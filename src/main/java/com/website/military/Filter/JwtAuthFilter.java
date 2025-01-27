package com.website.military.Filter;

import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.website.military.config.jwt.JwtProvider;
import com.website.military.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter{
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException{
        final String token = request.getHeader("Authorization");

        String username = null;

        if(token != null && !token.isEmpty()){
            String jwtToken = token.substring(7);

            username = jwtProvider.getUsernameFromToken(jwtToken);
        }

        if(username != null && !username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null){
            SecurityContextHolder.getContext().setAuthentication(getUserAuth(username));
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUserAuth(String username){
        var userInfo = userRepository.findById(Long.parseLong(username));

        return new UsernamePasswordAuthenticationToken(userInfo.get().getUserId(), userInfo.get().getPassword(), 
        Collections.singleton(new SimpleGrantedAuthority(userInfo.get().getUsername())));
    }
}
