package pet.service;

import org.springframework.web.multipart.MultipartFile;
import pet.dto.CommunityCommentCreateDTO;
import pet.vo.CommunityCommentVO;
import pet.vo.CommunityPostVO;

import java.util.List;

public interface CommunityService {
    /**
     * 发布动态（文字 + 可选图片）
     */
    CommunityPostVO createPost(Long userId, String content, MultipartFile[] files);

    /**
     * 动态列表（按时间倒序）
     */
    List<CommunityPostVO> listPosts(Long userId);

    /**
     * 删除动态（仅允许删除自己的动态）
     */
    void deletePost(Long userId, Long postId);

    /**
     * 获取动态评论列表
     */
    List<CommunityCommentVO> listComments(Long userId, Long postId);

    /**
     * 新增评论
     */
    CommunityCommentVO createComment(Long userId, Long postId, CommunityCommentCreateDTO dto);

    /**
     * 删除评论（仅允许删除自己的评论）
     */
    void deleteComment(Long userId, Long postId, Long commentId);
}

