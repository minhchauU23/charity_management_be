package dev.ptit.charitymanagement.dtos.response.donation;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignDTO;
import dev.ptit.charitymanagement.dtos.response.payment.PaymentDTO;
import dev.ptit.charitymanagement.dtos.response.user.UserDTO;
import dev.ptit.charitymanagement.entity.DonationState;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DonationDTO {
    String id;
    Long amount;
    DonationState state;
    CampaignDTO campaign;
    UserDTO donor;
    Boolean isAnonymous;
    PaymentDTO payment;
}
