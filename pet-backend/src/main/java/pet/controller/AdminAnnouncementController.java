package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pet.common.Result;
import pet.dto.AdminCreateAnnouncementDTO;
import pet.dto.AdminUpdateAnnouncementStatusDTO;
import pet.service.AdminAnnouncementService;
import pet.vo.AnnouncementVO;
import pet.vo.PageVO;

@RestController
@RequestMapping("/api/admin/announcements")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AdminAnnouncementService adminAnnouncementService;

    @GetMapping
    public Result<PageVO<AnnouncementVO>> page(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        try {
            return Result.success(adminAnnouncementService.page(userId, page, size, keyword, status));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<AnnouncementVO> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody AdminCreateAnnouncementDTO dto
    ) {
        try {
            return Result.success(adminAnnouncementService.create(
                    userId,
                    dto == null ? null : dto.getTitle(),
                    dto == null ? null : dto.getContent(),
                    dto == null ? null : dto.getStatus()
            ));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<AnnouncementVO> updateStatus(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id,
            @RequestBody AdminUpdateAnnouncementStatusDTO dto
    ) {
        try {
            return Result.success(adminAnnouncementService.updateStatus(userId, id, dto == null ? null : dto.getStatus()));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

