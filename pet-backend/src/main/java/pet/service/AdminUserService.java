package pet.service;

import pet.vo.AdminUserVO;
import pet.vo.PageVO;

public interface AdminUserService {
    PageVO<AdminUserVO> pageUsers(Long operatorUserId, int page, int size, String keyword, Integer status, String role);
    AdminUserVO updateUserStatus(Long operatorUserId, Long targetUserId, Integer status);
    AdminUserVO updateUserRole(Long operatorUserId, Long targetUserId, String role);
}
