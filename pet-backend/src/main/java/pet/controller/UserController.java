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
        UserVO userVO = userService.register(registerDTO);
        return Result.success(userVO);
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginDTO loginDTO) {
        UserVO userVO = userService.login(loginDTO);
        return Result.success(userVO);
    }

    /**
     * 获取当前用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<UserVO> me(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        UserVO userVO = userService.getCurrentUser(userId);
        return Result.success(userVO);
    }

    /**
     * 更新用户资料
     *
     * @param userId 用户ID
     * @param updateProfileDTO 更新信息
     * @return 更新后的用户信息
     */
    @PutMapping("/me")
    public Result<UserVO> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody UpdateProfileDTO updateProfileDTO) {
        UserVO userVO = userService.updateProfile(userId, updateProfileDTO);
        return Result.success(userVO);
    }

    /**
     * 上传头像
     *
     * @param userId 用户ID
     * @param file 头像文件
     * @return 更新后的用户信息
     */
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UserVO> uploadAvatar(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestPart("file") MultipartFile file) {
        UserVO userVO = userService.uploadAvatar(userId, file);
        return Result.success(userVO);
    }

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param updatePasswordDTO 密码信息
     * @return 更新后的用户信息
     */
    @PutMapping("/me/password")
    public Result<UserVO> updatePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        UserVO userVO = userService.updatePassword(userId, updatePasswordDTO);
        return Result.success(userVO);
    }
}
