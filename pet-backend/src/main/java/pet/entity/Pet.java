package pet.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("pet")
public class Pet {
    /**
     * 宠物ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 所属用户ID
     */
    private Long userId;
    /**
     * 宠物姓名
     */
    private String name;
    /**
     * 品种
     */
    private String breed;
    /**
     * 性别：0-公，1-母
     */
    private Integer gender; // 0-公，1-母
    /**
     * 生日
     */
    private LocalDate birthday;
    /**
     * 照片URL
     */
    private String photo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
