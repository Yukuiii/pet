package pet.service;

import pet.vo.KnowledgeArticleVO;
import pet.vo.KnowledgeCategoryVO;
import pet.vo.PageVO;

import java.util.List;

public interface AdminKnowledgeService {
    List<KnowledgeCategoryVO> listCategories(Long operatorUserId, String keyword);
    KnowledgeCategoryVO createCategory(Long operatorUserId, String name, Integer sort);
    KnowledgeCategoryVO updateCategory(Long operatorUserId, Long id, String name, Integer sort);
    void deleteCategory(Long operatorUserId, Long id);

    PageVO<KnowledgeArticleVO> pageArticles(Long operatorUserId, int page, int size, Long categoryId, String keyword);
    KnowledgeArticleVO getArticle(Long operatorUserId, Long id);
    KnowledgeArticleVO createArticle(Long operatorUserId, Long categoryId, String title, String summary, String cover, String content);
    KnowledgeArticleVO updateArticle(Long operatorUserId, Long id, Long categoryId, String title, String summary, String cover, String content);
    void deleteArticle(Long operatorUserId, Long id);
}

