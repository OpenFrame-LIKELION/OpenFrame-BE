package com.openframe.openframe.exception.user;

import com.openframe.openframe.common.ApiResponse;
import com.openframe.openframe.common.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

	NO_USER_INFO(HttpStatus.BAD_REQUEST, "400", "사용자 정보가 존재하지 않습니다.")
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ApiResponse<Void> getErrorResponse() {
		return ApiResponse.onFailure(code, message);
	}
}
