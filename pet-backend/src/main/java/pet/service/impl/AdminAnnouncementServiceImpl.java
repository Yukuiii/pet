package pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.entity.Announcement;
import pet.entity.User;
import pet.mapper.AnnouncementMapper;
import pet.mapper.UserMapper;
import pet.service.AdminAnnouncementService;
import pet.vo.AnnouncementVO;
import pet.vo.PageVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final UserMapper userMapper;

    @Override
    public PageVO<AnnouncementVO> page(Long operatorUserId, int page, int size, String keyword, Integer status) {
        ensureAdmin(operatorUserId);
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        LambdaQueryWrapper<Announcement> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            qw.and(w -> w.like(Announcement::getTitle, k).or().like(Announcement::getContent, k));
        }
        if (status != null) qw.eq(Announcement::getStatus, status);
        qw.orderByDesc(Announcement::getCreateTime);
        Page<Announcement> p = new Page<>(page, size);
        Page<Announcement> result = announcementMapper.selectPage(p, qw);
        List<AnnouncementVO> list = new ArrayList<>();
        for (Announcement a : result.getRecords()) list.add(toVO(a));
        return PageVO.of(result.getTotal(), list);
    }

    @Override
    public AnnouncementVO create(Long operatorUserId, String title, String content, Integer status) {
        ensureAdmin(operatorUserId);
        if (title == null || title.isBlank()) throw new RuntimeException("标题不能为空");
        if (content == null || content.isBlank()) throw new RuntimeException("内容不能为空");
        if (status == null) status = 0;
        if (status != 0 && status != 1) throw new RuntimeException("状态不合法");
        Announcement a = new Announcement();
        a.setTitle(title.trim());
        a.setContent(content.trim());
        a.setStatus(status);
        a.setCreateTime(LocalDateTime.now());
        a.setUpdateTime(LocalDateTime.now());
        announcementMapper.insert(a);
        return toVO(a);
    }

    @Override
    public AnnouncementVO updateStatus(Long operatorUserId, Long id, Integer status) {
        ensureAdmin(operatorUserId);
        if (id == null) throw new RuntimeException("公告ID不能为空");
        if (status == null || (status != 0 && status != 1)) throw new RuntimeException("状态不合法");
        Announcement a = announcementMapper.selectById(id);
        if (a == null) throw new RuntimeException("公告不存在");
        a.setStatus(status);
        a.setUpdateTime(LocalDateTime.now());
        announcementMapper.updateById(a);
        return toVO(a);
    }

    private void ensureAdmin(Long operatorUserId) {
        if (operatorUserId == null) throw new RuntimeException("未登录");
        User user = userMapper.selectById(operatorUserId);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getStatus() != null && user.getStatus() == 1) throw new RuntimeException("账号已被封禁");
        if (user.getRole() == null || !"admin".equals(user.getRole())) throw new RuntimeException("无管理员权限");
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

