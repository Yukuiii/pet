package pet.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("health_record")
public class HealthRecord {
    /**
     * 健康记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 宠物ID
     */
    private Long petId;
    /**
     * 记录时间
     */
    private LocalDateTime recordTime;
    /**
     * 标题/疫苗名称
     */
    private String title;
    /**
     * 内容/备注
     */
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
