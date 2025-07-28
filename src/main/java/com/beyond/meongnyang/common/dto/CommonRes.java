package com.beyond.meongnyang.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonRes<T> {
    private Boolean isSuccess;
    private T data;
    private CommonStatus status;

    // 성공 시 사용하는 생성자
    public static <T> CommonRes<T> ofSuccess(T data, int statusCode, String statusMsg) {
        return new CommonRes<>(true, data, new CommonStatus(statusCode, statusMsg));
    }

    // 실패 시 사용하는 생성자
    public static <T> CommonRes<T> ofFailure(int status, String message) {
        return new CommonRes<>(false, null, new CommonStatus(status, message));
    }
}
