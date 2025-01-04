package dev.ptit.charitymanagement.service.payment;

import dev.ptit.charitymanagement.dtos.Payment;
import dev.ptit.charitymanagement.dtos.response.payment.PaymentDTO;

import java.util.Map;

public interface IpnHandler {
    public Payment process(Map<String, String> params) ;
}
