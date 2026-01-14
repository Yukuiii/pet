package pet.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;
import pet.dto.LoginDTO;
import pet.dto.RegisterDTO;
import pet.dto.UpdatePasswordDTO;
import pet.dto.UpdateProfileDTO;
import pet.entity.User;
import pet.mapper.UserMapper;
import pet.service.UserService;
import pet.vo.UserVO;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    @Override
    public UserVO register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDTO.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, registerDTO.getEmail());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        // 使用MD5加密密码
        user.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
        user.setNickname(registerDTO.getUsername());
        user.setRole(0);
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        return convertToVO(user);
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录成功的用户信息
     */
    @Override
    public UserVO login(LoginDTO loginDTO) {
        // 根据用户名查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查账号状态
        if (user.getStatus() == 1) {
            throw new RuntimeException("账号已被封禁");
        }

        return convertToVO(user);
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = getActiveUser(userId);
        return convertToVO(user);
    }

    @Override
    public UserVO updateProfile(Long userId, UpdateProfileDTO updateProfileDTO) {
        User user = getActiveUser(userId);
        if (updateProfileDTO.getNickname() != null) {
            String nickname = updateProfileDTO.getNickname().trim();
            if (nickname.isEmpty()) {
                throw new RuntimeException("昵称不能为空");
            }
            user.setNickname(nickname);
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return convertToVO(user);
    }

    @Override
    public UserVO uploadAvatar(Long userId, MultipartFile file) {
        User user = getActiveUser(userId);
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择头像文件");
        }

        String contentType = file.getContentType();
        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new RuntimeException("仅支持上传 jpg/png/gif/webp 图片");
        }

        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };

        String filename = UUID.randomUUID() + extension;
        Path avatarDir = Paths.get(uploadDir, "avatars");
        try {
            Files.createDirectories(avatarDir);
            Path target = avatarDir.resolve(filename).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("头像上传失败");
        }

        user.setAvatar("/uploads/avatars/" + filename);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return convertToVO(user);
    }

    @Override
    public UserVO updatePassword(Long userId, UpdatePasswordDTO updatePasswordDTO) {
        User user = getActiveUser(userId);
        if (updatePasswordDTO.getOldPassword() == null || updatePasswordDTO.getOldPassword().isBlank()) {
            throw new RuntimeException("旧密码不能为空");
        }
        if (updatePasswordDTO.getNewPassword() == null || updatePasswordDTO.getNewPassword().isBlank()) {
            throw new RuntimeException("新密码不能为空");
        }
        if (updatePasswordDTO.getNewPassword().length() < 6) {
            throw new RuntimeException("新密码长度至少 6 位");
        }

        String encryptedOld = DigestUtils.md5DigestAsHex(updatePasswordDTO.getOldPassword().getBytes());
        if (!encryptedOld.equals(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        user.setPassword(DigestUtils.md5DigestAsHex(updatePasswordDTO.getNewPassword().getBytes()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return convertToVO(user);
    }

    /**
     * 将User实体转换为UserVO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        return vo;
    }

    private User getActiveUser(Long userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new RuntimeException("账号已被封禁");
        }
        return user;
    }
}
