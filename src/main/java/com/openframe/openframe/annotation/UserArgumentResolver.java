package com.openframe.openframe.annotation;

import com.openframe.openframe.api.user.service.UserService;
import com.openframe.openframe.domain.entity.User;
import com.openframe.openframe.exception.security.SecurityCustomException;
import com.openframe.openframe.exception.security.SecurityErrorCode;
import com.openframe.openframe.security.oauth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(UserResolver.class);
        boolean isOrganizationParameterType = parameter.getParameterType().isAssignableFrom(User.class);
        return hasParameterAnnotation && isOrganizationParameterType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            return userService.findByEmail(((CustomUserDetails) userDetails).getEmail());
        } catch (ClassCastException e) {
            // 로그아웃된 토큰
            throw new SecurityCustomException(SecurityErrorCode.UNAUTHORIZED);
        }
    }
}
