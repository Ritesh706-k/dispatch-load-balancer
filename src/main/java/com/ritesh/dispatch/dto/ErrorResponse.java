package com.ritesh.dispatch.dto;

import java.util.List;

public record ErrorResponse(String status, String message, List<String> errors) {
    public static ErrorResponse of(String message, List<String> errors) {
        return new ErrorResponse("error", message, errors);
    }
}
