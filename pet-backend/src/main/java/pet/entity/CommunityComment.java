package pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_comment")
public class CommunityComment {
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 动态ID
     */
    private Long postId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

