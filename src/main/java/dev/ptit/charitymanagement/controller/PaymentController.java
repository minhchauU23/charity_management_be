package dev.ptit.charitymanagement.controller;

import dev.ptit.charitymanagement.dtos.Payment;
import dev.ptit.charitymanagement.service.payment.IpnHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    IpnHandler ipnHandler;
    @GetMapping("/payment-return")
    public Payment paymentReturn(@RequestParam Map<String, String> params){
        log.info("[VNPay Ipn] Params: {}", params);
        return ipnHandler.process(params);
    }
}
