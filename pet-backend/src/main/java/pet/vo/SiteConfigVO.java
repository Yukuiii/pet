package pet.vo;

import lombok.Data;

@Data
public class SiteConfigVO {
    private Long id;
    private String siteName;
    private String logo;
    private String contactEmail;
    private String contactPhone;
    private String icp;
    private String footerText;
}

