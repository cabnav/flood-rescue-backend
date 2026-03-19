package com.floodrescue.backend.common.service;

import com.floodrescue.backend.common.config.EsmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@Slf4j
public class EsmsClient {

    private final EsmsProperties props;
    private final RestTemplate restTemplate;

    @Autowired
    public EsmsClient(EsmsProperties props, RestTemplateBuilder builder) {
        this.props = props;
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(15))
                .setReadTimeout(Duration.ofSeconds(15))
                .build();
    }

    /**
     * Generate a 6-digit numeric OTP.
     */
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int value = random.nextInt(900_000) + 100_000;
        return String.valueOf(value);
    }

    /**
     * Simple string equality check for OTP. Caller must supply the expected value (store with expiry elsewhere).
     */
    public boolean verifyOtp(String expectedOtp, String providedOtp) {
        return expectedOtp != null && expectedOtp.equals(providedOtp);
    }

    /**
     * Simple test call to verify credentials and connectivity with eSMS.
     * @param phone Vietnamese phone number (format accepted by eSMS, e.g. 8490xxxxxxx)
     * @return raw JSON response from eSMS
     */
    public String sendTestSms(String phone) {
        String otp = generateOtp();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(props.getBaseUrl() + "/SendMultipleMessage_V4_get")
                .queryParam("ApiKey", props.getApiKey())
                .queryParam("SecretKey", props.getSecretKey())
                .queryParam("Phone", phone)
                .queryParam("Content", otp + " la ma xac minh dang ky Baotrixemay cua ban")
                .queryParam("SmsType", props.getSmsType() != null ? props.getSmsType() : 2);

        if (props.getBrandName() != null && !props.getBrandName().isBlank()) {
            uriBuilder.queryParam("Brandname", props.getBrandName());
        }

        String response = restTemplate.getForObject(uriBuilder.build().encode().toUri(), String.class);
        log.info("eSMS response: {}", response);
        return response;
    }
}
