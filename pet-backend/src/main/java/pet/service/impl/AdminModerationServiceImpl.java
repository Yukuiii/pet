package pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.entity.CommunityComment;
import pet.entity.CommunityPost;
import pet.entity.CommunityPostImage;
import pet.entity.User;
import pet.mapper.CommunityCommentMapper;
import pet.mapper.CommunityPostImageMapper;
import pet.mapper.CommunityPostMapper;
import pet.mapper.UserMapper;
import pet.service.AdminModerationService;
import pet.vo.CommunityCommentVO;
import pet.vo.CommunityPostVO;
import pet.vo.CommunityUserVO;
import pet.vo.PageVO;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminModerationServiceImpl implements AdminModerationService {

    private final CommunityPostMapper postMapper;
    private final CommunityPostImageMapper imageMapper;
    private final CommunityCommentMapper commentMapper;
    private final UserMapper userMapper;

    @Override
    public PageVO<CommunityPostVO> pagePosts(Long operatorUserId, int page, int size, String keyword, Long authorId) {
        ensureAdmin(operatorUserId);
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        LambdaQueryWrapper<CommunityPost> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(CommunityPost::getContent, keyword.trim());
        }
        if (authorId != null) {
            qw.eq(CommunityPost::getUserId, authorId);
        }
        qw.orderByDesc(CommunityPost::getCreateTime);
        Page<CommunityPost> p = new Page<>(page, size);
        Page<CommunityPost> result = postMapper.selectPage(p, qw);
        List<CommunityPost> records = result.getRecords();
        if (records.isEmpty()) return PageVO.of(result.getTotal(), Collections.emptyList());
        Set<Long> userIds = new HashSet<>();
        List<Long> postIds = new ArrayList<>();
        for (CommunityPost r : records) {
            userIds.add(r.getUserId());
            postIds.add(r.getId());
        }
        Map<Long, User> usersById = new HashMap<>();
        if (!userIds.isEmpty()) {
            for (User u : userMapper.selectBatchIds(userIds)) usersById.put(u.getId(), u);
        }
        LambdaQueryWrapper<CommunityPostImage> iqw = new LambdaQueryWrapper<>();
        iqw.in(CommunityPostImage::getPostId, postIds).orderByAsc(CommunityPostImage::getId);
        List<CommunityPostImage> images = imageMapper.selectList(iqw);
        Map<Long, List<String>> imagesByPostId = new HashMap<>();
        for (CommunityPostImage img : images) {
            imagesByPostId.computeIfAbsent(img.getPostId(), k -> new ArrayList<>()).add(img.getUrl());
        }
        List<CommunityPostVO> list = new ArrayList<>();
        for (CommunityPost r : records) {
            list.add(toPostVO(r, usersById.get(r.getUserId()), imagesByPostId.getOrDefault(r.getId(), Collections.emptyList())));
        }
        return PageVO.of(result.getTotal(), list);
    }

    @Override
    public void deletePost(Long operatorUserId, Long postId) {
        ensureAdmin(operatorUserId);
        postMapper.deleteById(postId);
    }

    @Override
    public PageVO<CommunityCommentVO> pageComments(Long operatorUserId, int page, int size, String keyword, Long postId, Long authorId) {
        ensureAdmin(operatorUserId);
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        LambdaQueryWrapper<CommunityComment> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) qw.like(CommunityComment::getContent, keyword.trim());
        if (postId != null) qw.eq(CommunityComment::getPostId, postId);
        if (authorId != null) qw.eq(CommunityComment::getUserId, authorId);
        qw.orderByDesc(CommunityComment::getCreateTime);
        Page<CommunityComment> p = new Page<>(page, size);
        Page<CommunityComment> result = commentMapper.selectPage(p, qw);
        List<CommunityComment> records = result.getRecords();
        if (records.isEmpty()) return PageVO.of(result.getTotal(), Collections.emptyList());
        Set<Long> userIds = new HashSet<>();
        for (CommunityComment c : records) userIds.add(c.getUserId());
        Map<Long, User> usersById = new HashMap<>();
        for (User u : userMapper.selectBatchIds(userIds)) usersById.put(u.getId(), u);
        List<CommunityCommentVO> list = new ArrayList<>();
        for (CommunityComment c : records) list.add(toCommentVO(c, usersById.get(c.getUserId())));
        return PageVO.of(result.getTotal(), list);
    }

    @Override
    public void deleteComment(Long operatorUserId, Long commentId) {
        ensureAdmin(operatorUserId);
        commentMapper.deleteById(commentId);
    }

    private void ensureAdmin(Long operatorUserId) {
        if (operatorUserId == null) throw new RuntimeException("未登录");
        User user = userMapper.selectById(operatorUserId);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getStatus() != null && user.getStatus() == 1) throw new RuntimeException("账号已被封禁");
        if (user.getRole() == null || !"admin".equals(user.getRole())) throw new RuntimeException("无管理员权限");
    }

    private CommunityPostVO toPostVO(CommunityPost post, User author, List<String> images) {
        CommunityPostVO vo = new CommunityPostVO();
        vo.setId(post.getId());
        vo.setContent(post.getContent());
        vo.setImages(images == null ? Collections.emptyList() : images);
        vo.setAuthor(toUserVO(author == null ? post.getUserId() : author.getId(), author));
        vo.setCreateTime(post.getCreateTime());
        return vo;
    }

    private CommunityCommentVO toCommentVO(CommunityComment comment, User author) {
        CommunityCommentVO vo = new CommunityCommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setAuthor(toUserVO(author == null ? comment.getUserId() : author.getId(), author));
        vo.setCreateTime(comment.getCreateTime());
        return vo;
    }

    private CommunityUserVO toUserVO(Long userId, User user) {
        CommunityUserVO vo = new CommunityUserVO();
        vo.setId(userId);
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }
        return vo;
    }
}

