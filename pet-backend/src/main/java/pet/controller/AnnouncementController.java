package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pet.common.Result;
import pet.service.AnnouncementService;
import pet.vo.AnnouncementVO;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/latest")
    public Result<AnnouncementVO> latest() {
        try {
            return Result.success(announcementService.latest());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<List<AnnouncementVO>> listActive(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            return Result.success(announcementService.listActive(limit));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

