package com.openframe.openframe.security.handler;

import com.openframe.openframe.exception.security.SecurityCustomException;
import com.openframe.openframe.exception.security.SecurityErrorCode;
import com.openframe.openframe.security.jwt.JwtUtil;
import com.openframe.openframe.security.redis.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            log.info("[*] Logout Filter");

            String accessToken = jwtUtil.resolveAccessToken(request);

            redisUtil.saveAsValue(
                    accessToken,
                    "logout",
                    jwtUtil.getExpTime(accessToken),
                    TimeUnit.MILLISECONDS
            );

            redisUtil.delete(
                    jwtUtil.getUsername(accessToken) + "_refresh_token"
            );

        } catch (ExpiredJwtException e) {
            log.warn("[*] case : accessToken expired");
            throw new SecurityCustomException(SecurityErrorCode.TOKEN_EXPIRED);
        }
    }
}
