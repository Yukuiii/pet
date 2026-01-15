package pet.service;

import org.springframework.web.multipart.MultipartFile;
import pet.dto.AdminUpdateSiteConfigDTO;
import pet.vo.SiteConfigVO;

/**
 * 管理员网站配置服务接口
 */
public interface AdminSiteConfigService {
    /**
     * 获取网站配置
     *
     * @param operatorUserId 操作用户ID
     * @return 网站配置信息
     */
    SiteConfigVO get(Long operatorUserId);

    /**
     * 更新网站配置
     *
     * @param operatorUserId 操作用户ID
     * @param dto 更新信息
     * @return 更新后的网站配置
     */
    SiteConfigVO update(Long operatorUserId, AdminUpdateSiteConfigDTO dto);

    /**
     * 上传网站 Logo
     *
     * @param operatorUserId 操作用户ID
     * @param file Logo 图片文件
     * @return 更新后的网站配置
     */
    SiteConfigVO uploadLogo(Long operatorUserId, MultipartFile file);
}

