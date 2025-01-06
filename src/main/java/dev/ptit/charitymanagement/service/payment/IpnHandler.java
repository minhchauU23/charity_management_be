package dev.ptit.charitymanagement.service.payment;

import dev.ptit.charitymanagement.dtos.Payment;

import java.util.Map;

public interface IpnHandler {
    public Payment process(Map<String, String> params) ;
}
