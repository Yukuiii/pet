package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pet.common.Result;
import pet.service.SiteConfigService;
import pet.vo.SiteConfigVO;

@RestController
@RequestMapping("/api/site-config")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    @GetMapping
    public Result<SiteConfigVO> get() {
        try {
            return Result.success(siteConfigService.get());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

