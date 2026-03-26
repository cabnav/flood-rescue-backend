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
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class EsmsClient {

    private final EsmsProperties props;
    private final RestTemplate restTemplate;

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

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

    private void storeOtp(String phone, String otp) {
        otpStore.put(phone, new OtpEntry(otp, Instant.now().plus(OTP_TTL)));
    }

    /**
     * Verify OTP previously sent to the given phone. Removes entry on success or expiry.
     */
    public boolean verifyOtp(String phone, String providedOtp) {
        OtpEntry entry = otpStore.get(phone);
        if (entry == null) {
            return false;
        }
        if (Instant.now().isAfter(entry.expiresAt())) {
            otpStore.remove(phone);
            return false;
        }
        boolean match = entry.code().equals(providedOtp);
        if (match) {
            otpStore.remove(phone);
        }
        return match;
    }


    public String sendTestSms(String phone) {
        String otp = generateOtp();
        storeOtp(phone, otp);
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

    private record OtpEntry(String code, Instant expiresAt) {}
}
