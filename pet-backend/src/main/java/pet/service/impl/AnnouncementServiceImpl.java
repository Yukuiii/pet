package pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.entity.Announcement;
import pet.mapper.AnnouncementMapper;
import pet.service.AnnouncementService;
import pet.vo.AnnouncementVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public AnnouncementVO latest() {
        LambdaQueryWrapper<Announcement> qw = new LambdaQueryWrapper<>();
        qw.eq(Announcement::getStatus, 0);
        qw.orderByDesc(Announcement::getCreateTime).last("limit 1");
        Announcement a = announcementMapper.selectOne(qw);
        return a == null ? null : toVO(a);
    }

    @Override
    public List<AnnouncementVO> listActive(int limit) {
        if (limit < 1) limit = 10;
        if (limit > 100) limit = 100;
        LambdaQueryWrapper<Announcement> qw = new LambdaQueryWrapper<>();
        qw.eq(Announcement::getStatus, 0);
        qw.orderByDesc(Announcement::getCreateTime).last("limit " + limit);
        List<Announcement> list = announcementMapper.selectList(qw);
        if (list.isEmpty()) return Collections.emptyList();
        List<AnnouncementVO> res = new ArrayList<>();
        for (Announcement a : list) res.add(toVO(a));
        return res;
    }

    private AnnouncementVO toVO(Announcement a) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(a.getId());
        vo.setTitle(a.getTitle());
        vo.setContent(a.getContent());
        vo.setStatus(a.getStatus());
        vo.setCreateTime(a.getCreateTime());
        return vo;
    }
}

