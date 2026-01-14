package pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_post_image")
public class CommunityPostImage {
    /**
     * 动态图片ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 动态ID
     */
    private Long postId;

    /**
     * 图片URL
     */
    private String url;

    private LocalDateTime createTime;
}

