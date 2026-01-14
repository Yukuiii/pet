package pet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pet.common.Result;
import pet.dto.AdminCreateKnowledgeCategoryDTO;
import pet.dto.AdminUpdateKnowledgeCategoryDTO;
import pet.service.AdminKnowledgeService;
import pet.vo.KnowledgeCategoryVO;

import java.util.List;

@RestController
@RequestMapping("/api/admin/knowledge/categories")
@RequiredArgsConstructor
public class AdminKnowledgeCategoryController {

    private final AdminKnowledgeService adminKnowledgeService;

    @GetMapping
    public Result<List<KnowledgeCategoryVO>> list(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        try {
            return Result.success(adminKnowledgeService.listCategories(userId, keyword));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<KnowledgeCategoryVO> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody AdminCreateKnowledgeCategoryDTO dto
    ) {
        try {
            return Result.success(adminKnowledgeService.createCategory(
                    userId,
                    dto == null ? null : dto.getName(),
                    dto == null ? null : dto.getSort()
            ));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<KnowledgeCategoryVO> update(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id,
            @RequestBody AdminUpdateKnowledgeCategoryDTO dto
    ) {
        try {
            return Result.success(adminKnowledgeService.updateCategory(
                    userId,
                    id,
                    dto == null ? null : dto.getName(),
                    dto == null ? null : dto.getSort()
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
            adminKnowledgeService.deleteCategory(userId, id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

