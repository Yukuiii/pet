package pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.entity.KnowledgeArticle;
import pet.entity.KnowledgeCategory;
import pet.entity.User;
import pet.mapper.KnowledgeArticleMapper;
import pet.mapper.KnowledgeCategoryMapper;
import pet.mapper.UserMapper;
import pet.service.AdminKnowledgeService;
import pet.vo.KnowledgeArticleVO;
import pet.vo.KnowledgeCategoryVO;
import pet.vo.PageVO;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminKnowledgeServiceImpl implements AdminKnowledgeService {

    private final KnowledgeCategoryMapper categoryMapper;
    private final KnowledgeArticleMapper articleMapper;
    private final UserMapper userMapper;

    @Override
    public List<KnowledgeCategoryVO> listCategories(Long operatorUserId, String keyword) {
        ensureAdmin(operatorUserId);
        LambdaQueryWrapper<KnowledgeCategory> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.like(KnowledgeCategory::getName, keyword.trim());
        }
        qw.orderByDesc(KnowledgeCategory::getSort).orderByAsc(KnowledgeCategory::getId);
        List<KnowledgeCategory> list = categoryMapper.selectList(qw);
        List<KnowledgeCategoryVO> result = new ArrayList<>();
        for (KnowledgeCategory c : list) result.add(toCategoryVO(c));
        return result;
    }

    @Override
    public KnowledgeCategoryVO createCategory(Long operatorUserId, String name, Integer sort) {
        ensureAdmin(operatorUserId);
        if (name == null || name.isBlank()) throw new RuntimeException("分类名称不能为空");
        KnowledgeCategory c = new KnowledgeCategory();
        c.setName(name.trim());
        c.setSort(sort == null ? 0 : sort);
        c.setCreateTime(LocalDateTime.now());
        c.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(c);
        return toCategoryVO(c);
    }

    @Override
    public KnowledgeCategoryVO updateCategory(Long operatorUserId, Long id, String name, Integer sort) {
        ensureAdmin(operatorUserId);
        if (id == null) throw new RuntimeException("分类ID不能为空");
        KnowledgeCategory c = categoryMapper.selectById(id);
        if (c == null) throw new RuntimeException("分类不存在");
        if (name != null) {
            String n = name.trim();
            if (n.isEmpty()) throw new RuntimeException("分类名称不能为空");
            c.setName(n);
        }
        if (sort != null) c.setSort(sort);
        c.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(c);
        return toCategoryVO(c);
    }

    @Override
    public void deleteCategory(Long operatorUserId, Long id) {
        ensureAdmin(operatorUserId);
        if (id == null) throw new RuntimeException("分类ID不能为空");
        LambdaQueryWrapper<KnowledgeArticle> qw = new LambdaQueryWrapper<>();
        qw.eq(KnowledgeArticle::getCategoryId, id);
        if (articleMapper.selectCount(qw) > 0) {
            throw new RuntimeException("分类下存在文章，无法删除");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public PageVO<KnowledgeArticleVO> pageArticles(Long operatorUserId, int page, int size, Long categoryId, String keyword) {
        ensureAdmin(operatorUserId);
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        LambdaQueryWrapper<KnowledgeArticle> qw = new LambdaQueryWrapper<>();
        if (categoryId != null) qw.eq(KnowledgeArticle::getCategoryId, categoryId);
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            qw.and(w -> w.like(KnowledgeArticle::getTitle, k).or().like(KnowledgeArticle::getContent, k));
        }
        qw.orderByDesc(KnowledgeArticle::getCreateTime);
        Page<KnowledgeArticle> p = new Page<>(page, size);
        Page<KnowledgeArticle> result = articleMapper.selectPage(p, qw);
        List<KnowledgeArticle> records = result.getRecords();
        if (records.isEmpty()) return PageVO.of(result.getTotal(), Collections.emptyList());
        Set<Long> categoryIds = new HashSet<>();
        for (KnowledgeArticle a : records) categoryIds.add(a.getCategoryId());
        Map<Long, KnowledgeCategory> categoryById = new HashMap<>();
        for (KnowledgeCategory c : categoryMapper.selectBatchIds(categoryIds)) categoryById.put(c.getId(), c);
        List<KnowledgeArticleVO> list = new ArrayList<>();
        for (KnowledgeArticle a : records) {
            list.add(toArticleVO(a, categoryById.get(a.getCategoryId()), false));
        }
        return PageVO.of(result.getTotal(), list);
    }

    @Override
    public KnowledgeArticleVO getArticle(Long operatorUserId, Long id) {
        ensureAdmin(operatorUserId);
        if (id == null) throw new RuntimeException("文章ID不能为空");
        KnowledgeArticle a = articleMapper.selectById(id);
        if (a == null) throw new RuntimeException("文章不存在");
        KnowledgeCategory c = categoryMapper.selectById(a.getCategoryId());
        return toArticleVO(a, c, true);
    }

    @Override
    public KnowledgeArticleVO createArticle(Long operatorUserId, Long categoryId, String title, String summary, String cover, String content) {
        ensureAdmin(operatorUserId);
        if (categoryId == null) throw new RuntimeException("分类不能为空");
        if (categoryMapper.selectById(categoryId) == null) throw new RuntimeException("分类不存在");
        if (title == null || title.isBlank()) throw new RuntimeException("标题不能为空");
        if (content == null || content.isBlank()) throw new RuntimeException("正文不能为空");
        KnowledgeArticle a = new KnowledgeArticle();
        a.setCategoryId(categoryId);
        a.setTitle(title.trim());
        a.setSummary(trimToNull(summary));
        a.setCover(trimToNull(cover));
        a.setContent(content.trim());
        a.setCreateTime(LocalDateTime.now());
        a.setUpdateTime(LocalDateTime.now());
        articleMapper.insert(a);
        KnowledgeCategory c = categoryMapper.selectById(categoryId);
        return toArticleVO(a, c, true);
    }

    @Override
    public KnowledgeArticleVO updateArticle(Long operatorUserId, Long id, Long categoryId, String title, String summary, String cover, String content) {
        ensureAdmin(operatorUserId);
        if (id == null) throw new RuntimeException("文章ID不能为空");
        KnowledgeArticle a = articleMapper.selectById(id);
        if (a == null) throw new RuntimeException("文章不存在");
        if (categoryId != null) {
            if (categoryMapper.selectById(categoryId) == null) throw new RuntimeException("分类不存在");
            a.setCategoryId(categoryId);
        }
        if (title != null) {
            String t = title.trim();
            if (t.isEmpty()) throw new RuntimeException("标题不能为空");
            a.setTitle(t);
        }
        if (summary != null) a.setSummary(trimToNull(summary));
        if (cover != null) a.setCover(trimToNull(cover));
        if (content != null) {
            String c = content.trim();
            if (c.isEmpty()) throw new RuntimeException("正文不能为空");
            a.setContent(c);
        }
        a.setUpdateTime(LocalDateTime.now());
        articleMapper.updateById(a);
        KnowledgeCategory c = categoryMapper.selectById(a.getCategoryId());
        return toArticleVO(a, c, true);
    }

    @Override
    public void deleteArticle(Long operatorUserId, Long id) {
        ensureAdmin(operatorUserId);
        if (id == null) throw new RuntimeException("文章ID不能为空");
        articleMapper.deleteById(id);
    }

    private KnowledgeCategoryVO toCategoryVO(KnowledgeCategory c) {
        KnowledgeCategoryVO vo = new KnowledgeCategoryVO();
        vo.setId(c.getId());
        vo.setName(c.getName());
        vo.setSort(c.getSort());
        return vo;
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
        if (withContent) vo.setContent(a.getContent());
        return vo;
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void ensureAdmin(Long operatorUserId) {
        if (operatorUserId == null) throw new RuntimeException("未登录");
        User user = userMapper.selectById(operatorUserId);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getStatus() != null && user.getStatus() == 1) throw new RuntimeException("账号已被封禁");
        if (user.getRole() == null || !"admin".equals(user.getRole())) throw new RuntimeException("无管理员权限");
    }
}

