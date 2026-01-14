package pet.dto;

import lombok.Data;

@Data
public class AdminUpdateSiteConfigDTO {
    private String siteName;
    private String logo;
    private String contactEmail;
    private String contactPhone;
    private String icp;
    private String footerText;
}

