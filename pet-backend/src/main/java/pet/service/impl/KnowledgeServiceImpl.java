package pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.entity.KnowledgeArticle;
import pet.entity.KnowledgeCategory;
import pet.mapper.KnowledgeArticleMapper;
import pet.mapper.KnowledgeCategoryMapper;
import pet.service.KnowledgeService;
import pet.vo.KnowledgeArticleVO;
import pet.vo.KnowledgeCategoryVO;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeCategoryMapper categoryMapper;
    private final KnowledgeArticleMapper articleMapper;

    @Override
    public List<KnowledgeCategoryVO> listCategories() {
        LambdaQueryWrapper<KnowledgeCategory> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(KnowledgeCategory::getSort).orderByAsc(KnowledgeCategory::getId);
        List<KnowledgeCategory> list = categoryMapper.selectList(qw);
        List<KnowledgeCategoryVO> result = new ArrayList<>();
        for (KnowledgeCategory c : list) {
            KnowledgeCategoryVO vo = new KnowledgeCategoryVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            vo.setSort(c.getSort());
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<KnowledgeArticleVO> listArticles(Long categoryId, String keyword) {
        LambdaQueryWrapper<KnowledgeArticle> qw = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            qw.eq(KnowledgeArticle::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            qw.and(w -> w.like(KnowledgeArticle::getTitle, k).or().like(KnowledgeArticle::getContent, k));
        }
        qw.orderByDesc(KnowledgeArticle::getCreateTime);
        List<KnowledgeArticle> list = articleMapper.selectList(qw);
        if (list.isEmpty()) return Collections.emptyList();

        Set<Long> categoryIds = new HashSet<>();
        for (KnowledgeArticle a : list) categoryIds.add(a.getCategoryId());
        Map<Long, KnowledgeCategory> categoryById = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            for (KnowledgeCategory c : categoryMapper.selectBatchIds(categoryIds)) {
                categoryById.put(c.getId(), c);
            }
        }

        List<KnowledgeArticleVO> result = new ArrayList<>();
        for (KnowledgeArticle a : list) {
            KnowledgeCategory c = categoryById.get(a.getCategoryId());
            KnowledgeArticleVO vo = toArticleVO(a, c, false);
            result.add(vo);
        }
        return result;
    }

    @Override
    public KnowledgeArticleVO getArticle(Long id) {
        KnowledgeArticle a = articleMapper.selectById(id);
        if (a == null) {
            throw new RuntimeException("文章不存在");
        }
        KnowledgeCategory c = categoryMapper.selectById(a.getCategoryId());
        return toArticleVO(a, c, true);
    }

    private KnowledgeArticleVO toArticleVO(KnowledgeArticle a, KnowledgeCategory c, boolean withContent) {
        KnowledgeArticleVO vo = new KnowledgeArticleVO();
        vo.setId(a.getId());
        vo.setCategoryId(a.getCategoryId());
        vo.setCategoryName(c == null ? null : c.getName());
        vo.setTitle(a.getTitle());
        vo.setSummary(a.getSummary());
        vo.setCover(a.getCover());
        vo.setCreateTime(a.getCreateTime());
        if (withContent) {
            vo.setContent(a.getContent());
        }
        return vo;
    }
}

