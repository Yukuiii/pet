package pet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("site_config")
public class SiteConfig {
    @TableId
    private Long id;
    private String siteName;
    private String logo;
    private String contactEmail;
    private String contactPhone;
    private String icp;
    private String footerText;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

