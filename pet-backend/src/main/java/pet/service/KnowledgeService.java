package pet.service;

import pet.vo.KnowledgeArticleVO;
import pet.vo.KnowledgeCategoryVO;

import java.util.List;

public interface KnowledgeService {
    List<KnowledgeCategoryVO> listCategories();
    List<KnowledgeArticleVO> listArticles(Long categoryId, String keyword);
    KnowledgeArticleVO getArticle(Long id);
}

