package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pet.common.Result;
import pet.service.AdminModerationService;
import pet.vo.CommunityCommentVO;
import pet.vo.CommunityPostVO;
import pet.vo.PageVO;

@RestController
@RequestMapping("/api/admin/moderation")
@RequiredArgsConstructor
public class AdminModerationController {

    private final AdminModerationService moderationService;

    @GetMapping("/posts")
    public Result<PageVO<CommunityPostVO>> pagePosts(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "authorId", required = false) Long authorId
    ) {
        try {
            return Result.success(moderationService.pagePosts(userId, page, size, keyword, authorId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/posts/{postId}")
    public Result<Void> deletePost(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long postId
    ) {
        try {
            moderationService.deletePost(userId, postId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/comments")
    public Result<PageVO<CommunityCommentVO>> pageComments(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "postId", required = false) Long postId,
            @RequestParam(value = "authorId", required = false) Long authorId
    ) {
        try {
            return Result.success(moderationService.pageComments(userId, page, size, keyword, postId, authorId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long commentId
    ) {
        try {
            moderationService.deleteComment(userId, commentId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

