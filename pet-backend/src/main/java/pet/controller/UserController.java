package pet.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pet.common.Result;
import pet.dto.LoginDTO;
import pet.dto.RegisterDTO;
import pet.dto.UpdatePasswordDTO;
import pet.dto.UpdateProfileDTO;
import pet.service.UserService;
import pet.vo.UserVO;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO registerDTO) {
        try {
            UserVO userVO = userService.register(registerDTO);
            return Result.success(userVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginDTO loginDTO) {
        try {
            UserVO userVO = userService.login(loginDTO);
            return Result.success(userVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/me")
    public Result<UserVO> me(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            UserVO userVO = userService.getCurrentUser(userId);
            return Result.success(userVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/me")
    public Result<UserVO> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody UpdateProfileDTO updateProfileDTO) {
        try {
            UserVO userVO = userService.updateProfile(userId, updateProfileDTO);
            return Result.success(userVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UserVO> uploadAvatar(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestPart("file") MultipartFile file) {
        try {
            UserVO userVO = userService.uploadAvatar(userId, file);
            return Result.success(userVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/me/password")
    public Result<UserVO> updatePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        try {
            UserVO userVO = userService.updatePassword(userId, updatePasswordDTO);
            return Result.success(userVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
