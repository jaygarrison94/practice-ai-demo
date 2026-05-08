package com.privatebutler.interfaces.user;

import com.privatebutler.application.common.AuthApplicationService;
import com.privatebutler.application.user.UserLoginService;
import com.privatebutler.application.user.UserRegisterService;
import com.privatebutler.application.user.UserProfileService;
import com.privatebutler.infrastructure.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserRegisterService userRegisterService;
    private final UserLoginService userLoginService;
    private final UserProfileService userProfileService;
    private final AuthApplicationService authApplicationService;

    @PostMapping("/sms-code")
    public ApiResponse<String> sendSmsCode(@RequestParam @NotBlank String phone) {
        String code = userRegisterService.sendSmsCode(phone);
        return ApiResponse.success("验证码已发送", code);
    }

    @PostMapping("/register")
    public ApiResponse<UserRegisterService.RegisterResult> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterService.RegisterResult result = userRegisterService.register(
            request.getPhone(), request.getSmsCode(), request.getPassword(),
            request.getConfirmPassword(), request.isAgreementAccepted());
        return ApiResponse.success("注册成功", result);
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginService.LoginResult> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginService.LoginResult result = userLoginService.login(
            request.getPhone(), request.getPassword(), request.isRememberPassword());
        return ApiResponse.success("登录成功", result);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        Long userId = authApplicationService.getCurrentUserId(request);
        userLoginService.logout(userId);
        return ApiResponse.success("退出成功", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestParam String phone, @RequestParam String smsCode,
                                           @RequestParam String newPassword, @RequestParam String confirmPassword) {
        userLoginService.resetPassword(phone, smsCode, newPassword, confirmPassword);
        return ApiResponse.success("密码重置成功", null);
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileVO> getProfile(HttpServletRequest request) {
        Long userId = authApplicationService.getCurrentUserId(request);
        UserProfileService.UserProfileVO profile = userProfileService.getProfile(userId);
        UserProfileVO vo = new UserProfileVO(
            profile.userId(), profile.phone(), profile.nickname(), profile.avatar(),
            profile.gender(), profile.birthday(),
            profile.remindSoundEnabled(), profile.remindVibrationEnabled());
        return ApiResponse.success(vo);
    }

    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(HttpServletRequest request,
                                           @RequestParam(required = false) String nickname,
                                           @RequestParam(required = false) String avatar,
                                           @RequestParam(required = false) Integer gender,
                                           @RequestParam(required = false) LocalDate birthday) {
        Long userId = authApplicationService.getCurrentUserId(request);
        userProfileService.updateProfile(userId, nickname, avatar, gender, birthday);
        return ApiResponse.success("保存成功", null);
    }
}
