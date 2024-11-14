package com.openframe.openframe.api.user.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user")
@Tag(name = "[User] 유저 API", description = "유저 관련 정보 제공")
public class UserController {
}
