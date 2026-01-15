package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pet.common.Result;
import pet.dto.AdminUpdateSiteConfigDTO;
import pet.service.AdminSiteConfigService;
import pet.vo.SiteConfigVO;

/**
 * 管理员网站配置控制器
 */
@RestController
@RequestMapping("/api/admin/site-config")
@RequiredArgsConstructor
public class AdminSiteConfigController {

    private final AdminSiteConfigService adminSiteConfigService;

    /**
     * 获取网站配置
     *
     * @param userId 操作用户ID
     * @return 网站配置信息
     */
    @GetMapping
    public Result<SiteConfigVO> get(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            return Result.success(adminSiteConfigService.get(userId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新网站配置
     *
     * @param userId 操作用户ID
     * @param dto 更新信息
     * @return 更新后的网站配置
     */
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

    /**
     * 上传网站 Logo
     *
     * @param userId 操作用户ID
     * @param file Logo 图片文件
     * @return 更新后的网站配置
     */
    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<SiteConfigVO> uploadLogo(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            return Result.success(adminSiteConfigService.uploadLogo(userId, file));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

