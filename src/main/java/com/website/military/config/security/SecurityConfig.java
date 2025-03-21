package com.website.military.config.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.website.military.config.jwt.JwtAccessDeniedHandler;
import com.website.military.config.jwt.JwtAuthenticationEntryPoint;
import com.website.military.filter.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean 
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector){
        return new MvcRequestMatcher.Builder(introspector);
    }

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*")); // ⭐️ 허용할 origin
            config.setAllowCredentials(true);
            return config;
        };
    }
    
    @Bean
    public SecurityFilterChain config(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception{
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        MvcRequestMatcher[] permitAllWhiteList = {
            mvc.pattern("/api/auth/signup"),
            mvc.pattern("/api/auth/login"),
            mvc.pattern("/api/auth/check"),
            mvc.pattern("/swagger-ui.html"),  // Swagger UI 메인 페이지
            mvc.pattern("/swagger-ui/**"),  // Swagger UI 관련 자원
            mvc.pattern("/v3/api-docs/**"),  // Swagger API 문서
            mvc.pattern("/swagger-resources/**"),  // Swagger 자원들
            mvc.pattern("/api-docs/**"),  // Swagger API 문서들
            mvc.pattern("/api-docs"),  // Swagger API 문서 경로
            mvc.pattern("/webjars/**"),  // Swagger API 문서 경로
            mvc.pattern("/swagger/**"),  // Swagger API 문서 경로
            mvc.pattern("/"),  // Swagger UI 메인 페이지
            mvc.pattern("/api/auth/token-refresh"),  // 토큰 리프레쉬
        };  

        http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers(permitAllWhiteList).permitAll()
        .anyRequest().authenticated());

        http.formLogin(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource())); 
        http.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(conf -> conf
        .authenticationEntryPoint(authenticationEntryPoint)
        .accessDeniedHandler(accessDeniedHandler));

        return http.build();

    }

}
