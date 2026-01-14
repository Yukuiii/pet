package pet.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.entity.SiteConfig;
import pet.mapper.SiteConfigMapper;
import pet.service.SiteConfigService;
import pet.vo.SiteConfigVO;

@Service
@RequiredArgsConstructor
public class SiteConfigServiceImpl implements SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    @Override
    public SiteConfigVO get() {
        SiteConfig cfg = siteConfigMapper.selectById(1L);
        if (cfg == null) return null;
        return toVO(cfg);
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

