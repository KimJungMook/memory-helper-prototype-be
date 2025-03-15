package com.website.military.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.website.military.config.jwt.JwtProvider;
import com.website.military.config.redis.RedisUtil;
import com.website.military.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter{
    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain) throws ServletException, IOException{
        final String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
    
        // "Bearer " 제거 후 순수한 JWT 추출
        String jwtToken = token.substring(7);
    
        //  1. 블랙리스트에 있는지 먼저 확인 (로그아웃된 토큰 차단)
        // if (redisUtil.haskeyBlackList(jwtToken)) {
        //     response.setStatus(HttpStatus.UNAUTHORIZED.value());
        //     response.setContentType("application/json");
        //     response.setCharacterEncoding("UTF-8");
        //     // ObjectMapper를 사용해 JSON 변환
        //     ObjectMapper objectMapper = new ObjectMapper();
        //     Map<String, Object> errorResponse = new HashMap<>();
        //     errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        //     errorResponse.put("message", "이미 로그아웃된 계정입니다.");
        
        //     // JSON 형식으로 응답 전송
        //     String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        //     response.getWriter().write(jsonResponse);
        //     return;
        // }
    
        //  2. 토큰 유효성 검사
        // if (!jwtProvider.validateToken(jwtToken)) {
        //     response.setStatus(HttpStatus.UNAUTHORIZED.value());
        //     response.setContentType("application/json");
        //     response.setCharacterEncoding("UTF-8");
        //     ObjectMapper objectMapper = new ObjectMapper();
        //     Map<String, Object> errorResponse = new HashMap<>();
        //     errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        //     errorResponse.put("message", "유효하지 않은 토큰입니다.");
        
        //     // JSON 형식으로 응답 전송
        //     String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        //     response.getWriter().write(jsonResponse);
        //     return;
        // }
    
        //  3. 토큰이 유효하면 사용자 정보 추출
        String username = jwtProvider.getUserIdFromToken(jwtToken);
    
        //  4. SecurityContext에 사용자 정보 설정
        if (username != null && !username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(getUserAuth(username));
        }
    
        //  5. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUserAuth(String username){
        var userInfo = userRepository.findById(Long.parseLong(username));

        return new UsernamePasswordAuthenticationToken(userInfo.get().getUserId(), userInfo.get().getPassword(), 
        Collections.singleton(new SimpleGrantedAuthority(userInfo.get().getUsername())));
    }
}
