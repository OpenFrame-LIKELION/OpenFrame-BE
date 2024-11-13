package com.openframe.openframe.api.user.service;

import com.openframe.openframe.domain.entity.User;
import com.openframe.openframe.domain.repository.UserRepository;
import com.openframe.openframe.exception.user.UserCustomException;
import com.openframe.openframe.exception.user.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserCustomException(UserErrorCode.NO_USER_INFO));
    }
}
