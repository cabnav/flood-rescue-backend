package com.floodrescue.backend.common.controller;

import com.floodrescue.backend.common.service.EsmsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/esms")
@RequiredArgsConstructor
public class EsmsTestController {

    private final EsmsClient esmsClient;

    @PostMapping("/sendSMS")
    public ResponseEntity<Map<String, Object>> sendTest(@RequestParam String phone) {
        String rawResponse = esmsClient.sendTestSms(phone);
        return ResponseEntity.ok(Map.of(
                "phone", phone,
                "response", rawResponse
        ));
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestParam String phone,
                                                         @RequestParam String otp) {
        boolean valid = esmsClient.verifyOtp(phone, otp);
        return ResponseEntity.ok(Map.of(
                "phone", phone,
                "otpValid", valid
        ));
    }
}
