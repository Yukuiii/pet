package pet.service;

import org.springframework.web.multipart.MultipartFile;

import pet.dto.LoginDTO;
import pet.dto.RegisterDTO;
import pet.dto.UpdatePasswordDTO;
import pet.dto.UpdateProfileDTO;
import pet.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    UserVO register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录成功的用户信息
     */
    UserVO login(LoginDTO loginDTO);

    UserVO getCurrentUser(Long userId);

    UserVO updateProfile(Long userId, UpdateProfileDTO updateProfileDTO);

    UserVO uploadAvatar(Long userId, MultipartFile file);

    UserVO updatePassword(Long userId, UpdatePasswordDTO updatePasswordDTO);
}
