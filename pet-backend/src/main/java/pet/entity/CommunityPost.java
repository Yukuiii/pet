package pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_post")
public class CommunityPost {
    /**
     * 动态ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发布用户ID
     */
    private Long userId;

    /**
     * 动态内容
     */
    private String content;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

