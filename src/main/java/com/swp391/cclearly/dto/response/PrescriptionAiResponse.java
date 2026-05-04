package com.swp391.cclearly.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionAiResponse {
    @JsonProperty("od_sph")
    private String od_sph;
    
    @JsonProperty("od_cyl")
    private String od_cyl;
    
    @JsonProperty("od_axs")
    private String od_axs;
    
    @JsonProperty("od_add")
    private String od_add;
    
    @JsonProperty("os_sph")
    private String os_sph;
    
    @JsonProperty("os_cyl")
    private String os_cyl;
    
    @JsonProperty("os_axs")
    private String os_axs;
    
    @JsonProperty("os_add")
    private String os_add;
    
    private String pd;
    private String note;
}
