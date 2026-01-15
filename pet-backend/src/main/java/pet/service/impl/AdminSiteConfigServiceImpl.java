package pet.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.dto.AdminUpdateSiteConfigDTO;
import pet.entity.SiteConfig;
import pet.entity.User;
import pet.mapper.SiteConfigMapper;
import pet.mapper.UserMapper;
import pet.service.AdminSiteConfigService;
import pet.vo.SiteConfigVO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * 管理员网站配置服务实现类
 */
@Service
@RequiredArgsConstructor
public class AdminSiteConfigServiceImpl implements AdminSiteConfigService {

    private final SiteConfigMapper siteConfigMapper;
    private final UserMapper userMapper;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public SiteConfigVO get(Long operatorUserId) {
        ensureAdmin(operatorUserId);
        SiteConfig cfg = siteConfigMapper.selectById(1L);
        if (cfg == null) {
            cfg = new SiteConfig();
            cfg.setId(1L);
            cfg.setSiteName("宠物管理系统");
            cfg.setCreateTime(LocalDateTime.now());
            cfg.setUpdateTime(LocalDateTime.now());
            siteConfigMapper.insert(cfg);
        }
        return toVO(cfg);
    }

    @Override
    public SiteConfigVO update(Long operatorUserId, AdminUpdateSiteConfigDTO dto) {
        ensureAdmin(operatorUserId);
        SiteConfig cfg = siteConfigMapper.selectById(1L);
        if (cfg == null) {
            cfg = new SiteConfig();
            cfg.setId(1L);
            cfg.setCreateTime(LocalDateTime.now());
        }
        if (dto != null) {
            if (dto.getSiteName() != null) {
                String name = dto.getSiteName().trim();
                if (name.isEmpty()) throw new RuntimeException("站点名称不能为空");
                cfg.setSiteName(name);
            }
            if (dto.getLogo() != null) cfg.setLogo(trimToNull(dto.getLogo()));
            if (dto.getContactEmail() != null) cfg.setContactEmail(trimToNull(dto.getContactEmail()));
            if (dto.getContactPhone() != null) cfg.setContactPhone(trimToNull(dto.getContactPhone()));
            if (dto.getIcp() != null) cfg.setIcp(trimToNull(dto.getIcp()));
            if (dto.getFooterText() != null) cfg.setFooterText(trimToNull(dto.getFooterText()));
        }
        cfg.setUpdateTime(LocalDateTime.now());
        if (siteConfigMapper.selectById(1L) == null) {
            siteConfigMapper.insert(cfg);
        } else {
            siteConfigMapper.updateById(cfg);
        }
        return toVO(cfg);
    }

    /**
     * 上传网站 Logo
     *
     * @param operatorUserId 操作用户ID
     * @param file Logo 图片文件
     * @return 更新后的网站配置
     */
    @Override
    public SiteConfigVO uploadLogo(Long operatorUserId, MultipartFile file) {
        ensureAdmin(operatorUserId);

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择 Logo 文件");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new RuntimeException("仅支持上传 jpg/png/gif/webp 图片");
        }

        // 根据文件类型确定扩展名
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };

        // 生成唯一文件名并保存
        String filename = UUID.randomUUID() + extension;
        Path logoDir = Paths.get(uploadDir, "logos");
        try {
            Files.createDirectories(logoDir);
            Path target = logoDir.resolve(filename).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Logo 上传失败: " + e.getMessage());
        }

        // 更新网站配置中的 Logo 路径
        SiteConfig cfg = siteConfigMapper.selectById(1L);
        if (cfg == null) {
            cfg = new SiteConfig();
            cfg.setId(1L);
            cfg.setSiteName("宠物管理系统");
            cfg.setCreateTime(LocalDateTime.now());
        }
        cfg.setLogo("/uploads/logos/" + filename);
        cfg.setUpdateTime(LocalDateTime.now());

        if (siteConfigMapper.selectById(1L) == null) {
            siteConfigMapper.insert(cfg);
        } else {
            siteConfigMapper.updateById(cfg);
        }

        return toVO(cfg);
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void ensureAdmin(Long operatorUserId) {
        if (operatorUserId == null) throw new RuntimeException("未登录");
        User user = userMapper.selectById(operatorUserId);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getStatus() != null && user.getStatus() == 1) throw new RuntimeException("账号已被封禁");
        if (user.getRole() == null || !"admin".equals(user.getRole())) throw new RuntimeException("无管理员权限");
    }

    private SiteConfigVO toVO(SiteConfig cfg) {
        SiteConfigVO vo = new SiteConfigVO();
        vo.setId(cfg.getId());
        vo.setSiteName(cfg.getSiteName());
        vo.setLogo(cfg.getLogo());
        vo.setContactEmail(cfg.getContactEmail());
        vo.setContactPhone(cfg.getContactPhone());
        vo.setIcp(cfg.getIcp());
        vo.setFooterText(cfg.getFooterText());
        return vo;
    }
}

