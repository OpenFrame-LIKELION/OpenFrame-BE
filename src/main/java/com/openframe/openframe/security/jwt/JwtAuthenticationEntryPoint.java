package com.openframe.openframe.security.jwt;

import com.openframe.openframe.common.ApiResponse;
import com.openframe.openframe.exception.security.SecurityErrorCode;
import com.openframe.openframe.util.HttpResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 사용자가 인증되지 않은 상태에서 접근하려고 할 때 발생하는 예외 처리
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        ApiResponse<String> errorResponse = ApiResponse.onFailure(
                SecurityErrorCode.UNAUTHORIZED.getCode(),
                SecurityErrorCode.UNAUTHORIZED.getMessage(),
                authException.getMessage()
        );

        HttpResponseUtil.setErrorResponse(response, httpStatus, errorResponse);
    }

}
