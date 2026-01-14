package pet.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import pet.entity.User;
import pet.mapper.UserMapper;
import pet.service.AdminUserService;
import pet.vo.AdminUserVO;
import pet.vo.PageVO;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserMapper userMapper;

    @Override
    public PageVO<AdminUserVO> pageUsers(Long operatorUserId, int page, int size, String keyword, Integer status,
            String role) {
        ensureAdmin(operatorUserId);
        if (page < 1)
            page = 1;
        if (size < 1)
            size = 10;
        if (size > 100)
            size = 100;

        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            qw.and(w -> w.like(User::getUsername, k)
                    .or()
                    .like(User::getEmail, k)
                    .or()
                    .like(User::getNickname, k));
        }
        if (status != null)
            qw.eq(User::getStatus, status);
        if (role != null)
            qw.eq(User::getRole, role);
        qw.orderByDesc(User::getId);

        Page<User> p = new Page<>(page, size);
        Page<User> result = userMapper.selectPage(p, qw);
        List<AdminUserVO> list = new ArrayList<>();
        for (User u : result.getRecords()) {
            list.add(toVO(u));
        }
        return PageVO.of(result.getTotal(), list);
    }

    @Override
    public AdminUserVO updateUserStatus(Long operatorUserId, Long targetUserId, Integer status) {
        ensureAdmin(operatorUserId);
        if (targetUserId == null)
            throw new RuntimeException("用户ID不能为空");
        if (status == null || (status != 0 && status != 1))
            throw new RuntimeException("状态不合法");
        User user = userMapper.selectById(targetUserId);
        if (user == null)
            throw new RuntimeException("用户不存在");
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return toVO(user);
    }

    @Override
    public AdminUserVO updateUserRole(Long operatorUserId, Long targetUserId, String role) {
        ensureAdmin(operatorUserId);
        if (targetUserId == null)
            throw new RuntimeException("用户ID不能为空");
        if (role == null || (!"user".equals(role) && !"admin".equals(role)))
            throw new RuntimeException("角色不合法");
        User user = userMapper.selectById(targetUserId);
        if (user == null)
            throw new RuntimeException("用户不存在");
        user.setRole(role);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return toVO(user);
    }

    private void ensureAdmin(Long operatorUserId) {
        if (operatorUserId == null)
            throw new RuntimeException("未登录");
        User user = userMapper.selectById(operatorUserId);
        if (user == null)
            throw new RuntimeException("用户不存在");
        if (user.getStatus() != null && user.getStatus() == 1)
            throw new RuntimeException("账号已被封禁");
        if (user.getRole() == null || !"admin".equals(user.getRole()))
            throw new RuntimeException("无管理员权限");
    }

    private AdminUserVO toVO(User user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
