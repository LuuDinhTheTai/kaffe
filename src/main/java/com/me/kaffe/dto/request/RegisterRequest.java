package com.me.kaffe.dto.request;

import com.me.kaffe.dto.request.base.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterRequest extends BaseRequest {

    private String fullName;

    private String username;
    private String password;
    private String number;

    @Override
    public void validate() {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Number is required");
        }
        if (!number.matches("\\d+")) {
            throw new IllegalArgumentException("Number must contain only digits");
        }
    }
}
