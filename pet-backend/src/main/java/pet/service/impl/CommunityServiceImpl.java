package pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.dto.CommunityCommentCreateDTO;
import pet.entity.CommunityComment;
import pet.entity.CommunityPost;
import pet.entity.CommunityPostImage;
import pet.entity.User;
import pet.mapper.CommunityCommentMapper;
import pet.mapper.CommunityPostImageMapper;
import pet.mapper.CommunityPostMapper;
import pet.mapper.UserMapper;
import pet.service.CommunityService;
import pet.vo.CommunityCommentVO;
import pet.vo.CommunityPostVO;
import pet.vo.CommunityUserVO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityPostMapper postMapper;
    private final CommunityPostImageMapper imageMapper;
    private final CommunityCommentMapper commentMapper;
    private final UserMapper userMapper;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public CommunityPostVO createPost(Long userId, String content, MultipartFile[] files) {
        User user = getActiveUser(userId);
        if (content == null || content.isBlank()) {
            throw new RuntimeException("动态内容不能为空");
        }

        CommunityPost post = new CommunityPost();
        post.setUserId(userId);
        post.setContent(content.trim());
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        postMapper.insert(post);

        List<String> urls = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;
                urls.add(saveImage(file, "community"));
            }
            for (String url : urls) {
                CommunityPostImage img = new CommunityPostImage();
                img.setPostId(post.getId());
                img.setUrl(url);
                img.setCreateTime(LocalDateTime.now());
                imageMapper.insert(img);
            }
        }

        return toPostVO(post, user, urls);
    }

    @Override
    public List<CommunityPostVO> listPosts(Long userId) {
        ensureUserId(userId);
        LambdaQueryWrapper<CommunityPost> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(CommunityPost::getCreateTime);
        List<CommunityPost> posts = postMapper.selectList(qw);
        if (posts.isEmpty()) return Collections.emptyList();

        Set<Long> userIds = new HashSet<>();
        List<Long> postIds = new ArrayList<>();
        for (CommunityPost p : posts) {
            userIds.add(p.getUserId());
            postIds.add(p.getId());
        }

        Map<Long, User> usersById = new HashMap<>();
        if (!userIds.isEmpty()) {
            for (User u : userMapper.selectBatchIds(userIds)) {
                usersById.put(u.getId(), u);
            }
        }

        LambdaQueryWrapper<CommunityPostImage> iqw = new LambdaQueryWrapper<>();
        iqw.in(CommunityPostImage::getPostId, postIds).orderByAsc(CommunityPostImage::getId);
        List<CommunityPostImage> images = imageMapper.selectList(iqw);
        Map<Long, List<String>> imagesByPostId = new HashMap<>();
        for (CommunityPostImage img : images) {
            imagesByPostId.computeIfAbsent(img.getPostId(), k -> new ArrayList<>()).add(img.getUrl());
        }

        List<CommunityPostVO> result = new ArrayList<>();
        for (CommunityPost p : posts) {
            User author = usersById.get(p.getUserId());
            result.add(toPostVO(p, author, imagesByPostId.getOrDefault(p.getId(), Collections.emptyList())));
        }
        return result;
    }

    @Override
    public void deletePost(Long userId, Long postId) {
        ensureUserId(userId);
        CommunityPost post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("动态不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该动态");
        }
        postMapper.deleteById(postId);
    }

    @Override
    public List<CommunityCommentVO> listComments(Long userId, Long postId) {
        ensureUserId(userId);
        CommunityPost post = postMapper.selectById(postId);
        if (post == null) throw new RuntimeException("动态不存在");

        LambdaQueryWrapper<CommunityComment> qw = new LambdaQueryWrapper<>();
        qw.eq(CommunityComment::getPostId, postId).orderByAsc(CommunityComment::getCreateTime);
        List<CommunityComment> list = commentMapper.selectList(qw);
        if (list.isEmpty()) return Collections.emptyList();

        Set<Long> userIds = new HashSet<>();
        for (CommunityComment c : list) userIds.add(c.getUserId());
        Map<Long, User> usersById = new HashMap<>();
        for (User u : userMapper.selectBatchIds(userIds)) usersById.put(u.getId(), u);

        List<CommunityCommentVO> result = new ArrayList<>();
        for (CommunityComment c : list) {
            result.add(toCommentVO(c, usersById.get(c.getUserId())));
        }
        return result;
    }

    @Override
    public CommunityCommentVO createComment(Long userId, Long postId, CommunityCommentCreateDTO dto) {
        User user = getActiveUser(userId);
        CommunityPost post = postMapper.selectById(postId);
        if (post == null) throw new RuntimeException("动态不存在");
        if (dto == null || dto.getContent() == null || dto.getContent().isBlank()) {
            throw new RuntimeException("评论内容不能为空");
        }
        CommunityComment comment = new CommunityComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(dto.getContent().trim());
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        return toCommentVO(comment, user);
    }

    @Override
    public void deleteComment(Long userId, Long postId, Long commentId) {
        ensureUserId(userId);
        CommunityComment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getPostId().equals(postId)) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该评论");
        }
        commentMapper.deleteById(commentId);
    }

    private String saveImage(MultipartFile file, String dirName) {
        String contentType = file.getContentType();
        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new RuntimeException("仅支持上传 jpg/png/gif/webp 图片");
        }
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
        String filename = UUID.randomUUID() + extension;
        Path imgDir = Paths.get(uploadDir, "community_images", dirName);
        try {
            Files.createDirectories(imgDir);
            Path target = imgDir.resolve(filename).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("图片上传失败");
        }
        return "/uploads/community_images/" + dirName + "/" + filename;
    }

    private User getActiveUser(Long userId) {
        ensureUserId(userId);
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getStatus() != null && user.getStatus() == 1) throw new RuntimeException("账号已被封禁");
        return user;
    }

    private void ensureUserId(Long userId) {
        if (userId == null) throw new RuntimeException("未登录");
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

