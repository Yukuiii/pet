package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pet.common.Result;
import pet.dto.AdminUpdateSiteConfigDTO;
import pet.service.AdminSiteConfigService;
import pet.vo.SiteConfigVO;

@RestController
@RequestMapping("/api/admin/site-config")
@RequiredArgsConstructor
public class AdminSiteConfigController {

    private final AdminSiteConfigService adminSiteConfigService;

    @GetMapping
    public Result<SiteConfigVO> get(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            return Result.success(adminSiteConfigService.get(userId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<SiteConfigVO> update(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody AdminUpdateSiteConfigDTO dto
    ) {
        try {
            return Result.success(adminSiteConfigService.update(userId, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

