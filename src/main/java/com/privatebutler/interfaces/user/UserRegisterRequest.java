package com.privatebutler.interfaces.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank(message = "请输入手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String phone;

    @NotBlank(message = "请输入验证码")
    private String smsCode;

    @NotBlank(message = "请输入密码")
    private String password;

    @NotBlank(message = "请确认密码")
    private String confirmPassword;

    @AssertTrue(message = "请勾选用户协议与隐私政策")
    private boolean agreementAccepted;
}
