package pet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pet.common.Result;
import pet.dto.AdminUpdateUserRoleDTO;
import pet.dto.AdminUpdateUserStatusDTO;
import pet.service.AdminUserService;
import pet.vo.AdminUserVO;
import pet.vo.PageVO;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public Result<PageVO<AdminUserVO>> pageUsers(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "role", required = false) String role) {
        try {
            return Result.success(adminUserService.pageUsers(userId, page, size, keyword, status, role));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<AdminUserVO> updateStatus(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable("id") Long targetUserId,
            @RequestBody AdminUpdateUserStatusDTO dto) {
        try {
            return Result.success(
                    adminUserService.updateUserStatus(userId, targetUserId, dto == null ? null : dto.getStatus()));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/role")
    public Result<AdminUserVO> updateRole(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable("id") Long targetUserId,
            @RequestBody AdminUpdateUserRoleDTO dto) {
        try {
            return Result
                    .success(adminUserService.updateUserRole(userId, targetUserId, dto == null ? null : dto.getRole()));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
