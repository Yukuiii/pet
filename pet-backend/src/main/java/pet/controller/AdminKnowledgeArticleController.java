package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pet.common.Result;
import pet.dto.AdminCreateKnowledgeArticleDTO;
import pet.dto.AdminUpdateKnowledgeArticleDTO;
import pet.service.AdminKnowledgeService;
import pet.vo.KnowledgeArticleVO;
import pet.vo.PageVO;

@RestController
@RequestMapping("/api/admin/knowledge/articles")
@RequiredArgsConstructor
public class AdminKnowledgeArticleController {

    private final AdminKnowledgeService adminKnowledgeService;

    @GetMapping
    public Result<PageVO<KnowledgeArticleVO>> page(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        try {
            return Result.success(adminKnowledgeService.pageArticles(userId, page, size, categoryId, keyword));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<KnowledgeArticleVO> get(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id
    ) {
        try {
            return Result.success(adminKnowledgeService.getArticle(userId, id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<KnowledgeArticleVO> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody AdminCreateKnowledgeArticleDTO dto
    ) {
        try {
            return Result.success(adminKnowledgeService.createArticle(
                    userId,
                    dto == null ? null : dto.getCategoryId(),
                    dto == null ? null : dto.getTitle(),
                    dto == null ? null : dto.getSummary(),
                    dto == null ? null : dto.getCover(),
                    dto == null ? null : dto.getContent()
            ));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<KnowledgeArticleVO> update(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id,
            @RequestBody AdminUpdateKnowledgeArticleDTO dto
    ) {
        try {
            return Result.success(adminKnowledgeService.updateArticle(
                    userId,
                    id,
                    dto == null ? null : dto.getCategoryId(),
                    dto == null ? null : dto.getTitle(),
                    dto == null ? null : dto.getSummary(),
                    dto == null ? null : dto.getCover(),
                    dto == null ? null : dto.getContent()
            ));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id
    ) {
        try {
            adminKnowledgeService.deleteArticle(userId, id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

