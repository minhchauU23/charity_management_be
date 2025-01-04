package dev.ptit.charitymanagement.service.payment;

import dev.ptit.charitymanagement.dtos.Payment;
import dev.ptit.charitymanagement.dtos.response.payment.PaymentDTO;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.service.donation.DonationService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
@RequiredArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VNPayIpnHandler implements IpnHandler{
    VNPayService vnPayService;
    DonationService donationService;
    @Override
    public Payment process(Map<String, String> params) {
        if (!vnPayService.verifyIpn(params)) {
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID);
        }

        Payment response;
        var txnRef = params.get(VNPayParams.TXN_REF);

        String donationId  = txnRef;
        String code = params.get("vnp_ResponseCode");
        if(code.equals("00")){
            donationService.markPaid(donationId);
            log.info("Success mark payment");
            return Payment.builder()
                    .code("00")
                    .message("SUCCESS")
                    .build();
        }
        donationService.markFailed(donationId);
        return Payment.builder()
                .code(code)
                .message("Unknown error")
                .build();

    }
}
