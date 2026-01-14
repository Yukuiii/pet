package pet.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pet.common.Result;
import pet.dto.CommunityCommentCreateDTO;
import pet.service.CommunityService;
import pet.vo.CommunityCommentVO;
import pet.vo.CommunityPostVO;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    /**
     * 发布动态（文字 + 可选图片）
     */
    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<CommunityPostVO> createPost(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestPart("content") String content,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        try {
            return Result.success(communityService.createPost(userId, content, files));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 动态列表
     */
    @GetMapping("/posts")
    public Result<List<CommunityPostVO>> listPosts(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            return Result.success(communityService.listPosts(userId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除动态（仅允许删除自己的动态）
     */
    @DeleteMapping("/posts/{postId}")
    public Result<Void> deletePost(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId) {
        try {
            communityService.deletePost(userId, postId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public Result<List<CommunityCommentVO>> listComments(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId) {
        try {
            return Result.success(communityService.listComments(userId, postId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增评论
     */
    @PostMapping("/posts/{postId}/comments")
    public Result<CommunityCommentVO> createComment(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId,
            @RequestBody CommunityCommentCreateDTO dto) {
        try {
            return Result.success(communityService.createComment(userId, postId, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除评论（仅允许删除自己的评论）
     */
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public Result<Void> deleteComment(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        try {
            communityService.deleteComment(userId, postId, commentId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
