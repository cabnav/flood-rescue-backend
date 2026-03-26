package com.floodrescue.backend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "esms")
public class EsmsProperties {
    /** Base URL for eSMS API, e.g. https://rest.esms.vn/MainService.svc/json */
    private String baseUrl;
    private String apiKey;
    private String secretKey;
    /** Optional brand name registered with eSMS */
    private String brandName;
    /** SmsType flag from eSMS docs (2 for BrandName OTP/notify) */
    private Integer smsType;
}

