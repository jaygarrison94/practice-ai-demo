package com.privatebutler.interfaces.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank(message = "请输入手机号")
    private String phone;

    @NotBlank(message = "请输入密码")
    private String password;

    private boolean rememberPassword;
}
