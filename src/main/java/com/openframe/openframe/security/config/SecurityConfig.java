package com.openframe.openframe.security.config;

import com.openframe.openframe.security.handler.CustomLogoutHandler;
import com.openframe.openframe.security.jwt.JwtAccessDeniedHandler;
import com.openframe.openframe.security.jwt.JwtAuthenticationEntryPoint;
import com.openframe.openframe.security.jwt.JwtFilter;
import com.openframe.openframe.security.jwt.JwtUtil;
import com.openframe.openframe.security.oauth.KakaoLoginAdapter;
import com.openframe.openframe.security.oauth.OAuthLoginFailureHandler;
import com.openframe.openframe.security.oauth.OAuthLoginSuccessHandler;
import com.openframe.openframe.security.redis.util.RedisUtil;
import com.openframe.openframe.util.HttpResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KakaoLoginAdapter kakaoLoginAdapter;
    private final OAuthLoginSuccessHandler oAuth2SuccessHandler;
    private final OAuthLoginFailureHandler oAuth2FailureHandler;

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RedisUtil redisUtil, JwtUtil jwtUtil) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authz -> {
                    // 접근 허용
                    authz.requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-resources/**",
                            "/v3/api-docs/**",
                            "/page/**").permitAll();
                    // 그 외의 모든 요청은 인증 필요
                    authz.anyRequest().authenticated();
                })

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(kakaoLoginAdapter))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler))

                .addFilterAt(jwtFilter, UsernamePasswordAuthenticationFilter.class)


                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler));

        http
                .logout(logout -> logout
                        .logoutUrl("/api/v1/users/logout")
                        .addLogoutHandler(new CustomLogoutHandler(redisUtil, jwtUtil))
                        .logoutSuccessHandler((request, response, authentication)
                                -> HttpResponseUtil.setSuccessResponse(
                                response,
                                HttpStatus.OK,
                                "로그아웃 성공"
                        ))
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
