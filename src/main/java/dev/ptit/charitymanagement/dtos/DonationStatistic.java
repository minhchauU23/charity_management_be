package dev.ptit.charitymanagement.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DonationStatistic {
    Long totalAcceptDonation;
    Long totalAmountRaised;
    Long totalAmountDelivered;
    List<Donation> latestDonations;
}
