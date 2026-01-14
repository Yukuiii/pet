package pet.service;

import pet.dto.AdminUpdateSiteConfigDTO;
import pet.vo.SiteConfigVO;

public interface AdminSiteConfigService {
    SiteConfigVO get(Long operatorUserId);
    SiteConfigVO update(Long operatorUserId, AdminUpdateSiteConfigDTO dto);
}

