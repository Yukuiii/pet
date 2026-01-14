package pet.service;

import pet.vo.CommunityCommentVO;
import pet.vo.CommunityPostVO;
import pet.vo.PageVO;

public interface AdminModerationService {
    PageVO<CommunityPostVO> pagePosts(Long operatorUserId, int page, int size, String keyword, Long authorId);
    void deletePost(Long operatorUserId, Long postId);
    PageVO<CommunityCommentVO> pageComments(Long operatorUserId, int page, int size, String keyword, Long postId, Long authorId);
    void deleteComment(Long operatorUserId, Long commentId);
}

