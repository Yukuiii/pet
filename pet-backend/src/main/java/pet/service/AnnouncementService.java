package pet.service;

import pet.vo.AnnouncementVO;

import java.util.List;

public interface AnnouncementService {
    AnnouncementVO latest();
    List<AnnouncementVO> listActive(int limit);
}

