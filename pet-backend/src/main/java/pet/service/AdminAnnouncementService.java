package pet.service;

import pet.vo.AnnouncementVO;
import pet.vo.PageVO;

public interface AdminAnnouncementService {
    PageVO<AnnouncementVO> page(Long operatorUserId, int page, int size, String keyword, Integer status);
    AnnouncementVO create(Long operatorUserId, String title, String content, Integer status);
    AnnouncementVO updateStatus(Long operatorUserId, Long id, Integer status);
}

