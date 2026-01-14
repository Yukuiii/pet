package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pet.common.Result;
import pet.service.KnowledgeService;
import pet.vo.KnowledgeArticleVO;
import pet.vo.KnowledgeCategoryVO;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @GetMapping("/categories")
    public Result<List<KnowledgeCategoryVO>> listCategories() {
        try {
            return Result.success(knowledgeService.listCategories());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/articles")
    public Result<List<KnowledgeArticleVO>> listArticles(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        try {
            return Result.success(knowledgeService.listArticles(categoryId, keyword));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/articles/{id}")
    public Result<KnowledgeArticleVO> getArticle(@PathVariable Long id) {
        try {
            return Result.success(knowledgeService.getArticle(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

