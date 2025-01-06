package dev.ptit.charitymanagement.service.donation;

import dev.ptit.charitymanagement.dtos.*;
import dev.ptit.charitymanagement.entity.*;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.CampaignRepository;
import dev.ptit.charitymanagement.repository.DonationRepository;
import dev.ptit.charitymanagement.repository.NotificationTemplateRepository;
import dev.ptit.charitymanagement.service.notification.impl.NotificationService;
import dev.ptit.charitymanagement.service.payment.PaymentService;
import dev.ptit.charitymanagement.service.websocket.CampaignMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DonationService {
    CampaignRepository campaignRepository;
    DonationRepository donationRepository;
    PaymentService paymentService;
    CampaignMessageService messageService;
    NotificationTemplateRepository notificationTemplateRepository;
    NotificationService notificationService;

    public Donation donate(Donation request, UserEntity donor){
        CampaignEntity campaignEntity = campaignRepository.findById(request.getCampaign().getId())
                .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED))
                ;
        if(campaignEntity.getCurrentStatus().equals(CampaignStatus.STARTED)){
            DonationEntity donationEntity = new DonationEntity();
            donationEntity.setCreatedAt(LocalDateTime.now());
            donationEntity.setAnonymousDonation(request.getIsAnonymous());
            donationEntity.setAmount(request.getAmount());
            donationEntity.setDonor(donor);
            donationEntity.setCampaign(campaignEntity);
            donationEntity.setState(DonationState.PENDING);
            donationEntity = donationRepository.save(donationEntity);

            Donation  response = Donation.builder()
                    .id(donationEntity.getId())
                    .amount(donationEntity.getAmount())
                    .state(donationEntity.getState())
                    .createdAt(donationEntity.getCreatedAt())
                    .donor(donationEntity.isAnonymousDonation()?null: User.builder()
                            .id(donationEntity.getDonor().getId())
                            .email(donationEntity.getDonor().getEmail())
                            .avatar(donationEntity.getDonor().getAvatar())
                            .build())
                    .isAnonymous(donationEntity.isAnonymousDonation())
                    .campaign(Campaign.builder()
                            .id(donationEntity.getCampaign().getId())
                            .totalAmountRaised(donationEntity.getCampaign().getDonations().stream()
                                    .mapToLong( donation -> {
                                                if(donation.getState().equals(DonationState.ACCEPT)){
                                                    return donation.getAmount();
                                                }
                                                return 0;
                                            }
                                    )
                                    .sum())
                            .totalNumberDonations(donationEntity.getCampaign().getDonations().stream()
                                    .mapToLong( donation -> {
                                                if(donation.getState().equals(DonationState.ACCEPT)){
                                                    return 1;
                                                }
                                                return 0;
                                            }
                                    )
                                    .sum())
                            .build())
                    .build();

            Payment payment = Payment.builder()
                    .donation(response)
                    .ipAddress(request.getIpAddress())
                    .build();
             payment = paymentService.createPaymentUrl(payment);

            response.setPayment(payment);

            messageService.newPendingDonations(response);
            return response;
        }
        throw new AppException(ErrorCode.DONATION_INVALID);

    }

    public Donation markPaid(String id){
        DonationEntity donationEntity = donationRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.BAD_REQUEST));
        donationEntity.setState(DonationState.ACCEPT);
        donationEntity = donationRepository.save(donationEntity);
        Donation response = Donation.builder()
                .id(donationEntity.getId())
                .amount(donationEntity.getAmount())
                .state(donationEntity.getState())
                .createdAt(donationEntity.getCreatedAt())
                .donor(donationEntity.isAnonymousDonation()?null: User.builder()
                        .id(donationEntity.getDonor().getId())
                        .email(donationEntity.getDonor().getEmail())
                        .avatar(donationEntity.getDonor().getAvatar())
                        .build())
                .isAnonymous(donationEntity.isAnonymousDonation())
                .campaign(Campaign.builder()
                        .id(donationEntity.getCampaign().getId())
                        .totalAmountRaised(donationEntity.getCampaign().getDonations().stream()
                                .mapToLong( donation -> {
                                            if(donation.getState().equals(DonationState.ACCEPT)){
                                                return donation.getAmount();
                                            }
                                            return 0;
                                        }
                                )
                                .sum())
                        .totalNumberDonations(donationEntity.getCampaign().getDonations().stream()
                                .mapToLong( donation -> {
                                            if(donation.getState().equals(DonationState.ACCEPT)){
                                                return 1;
                                            }
                                            return 0;
                                        }
                                )
                                .sum())
                        .build())
                .build();
        messageService.newAcceptDonations(response);

        NotificationTemplateEntity template = notificationTemplateRepository.findByName("THANK_FOR_DONATION")
                .orElseThrow(() ->new AppException(ErrorCode.NOTIFICATION_TEMPLATE_NOT_EXISTED));

        String data = "{\"firstName\":\""+donationEntity.getDonor().getFirstName() +"\"," +
                "\"lastName\":\""+donationEntity.getDonor().getLastName() +"\"," +
                "\"amount\":\""+donationEntity.getAmount() +"\"," +
                "\"campaignTitle\":\""+ donationEntity.getCampaign().getTitle() + "\"}";
        Notification notification = Notification.builder()
                .type(NotificationType.EMAIL)
                .data(data)
                .destination(User.builder()
                        .email(donationEntity.getDonor().getEmail())
                        .id(donationEntity.getDonor().getId())
                        .firstName(donationEntity.getDonor().getFirstName())
                        .lastName(donationEntity.getDonor().getLastName())
                        .build())
                .template(NotificationTemplate.builder()
                        .id(template.getId())
                        .name(template.getName())
                        .build())
                .build();
        notificationService.create(notification);

        return response;
    }

    public Donation markFailed(String id){
        DonationEntity donationEntity = donationRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.BAD_REQUEST));
        donationEntity.setState(DonationState.FAILED);
        donationEntity = donationRepository.save(donationEntity);
        Donation response = Donation.builder()
                .id(donationEntity.getId())
                .amount(donationEntity.getAmount())
                .state(donationEntity.getState())
                .createdAt(donationEntity.getCreatedAt())
                .donor(donationEntity.isAnonymousDonation()?null: User.builder()
                        .id(donationEntity.getDonor().getId())
                        .email(donationEntity.getDonor().getEmail())
                        .avatar(donationEntity.getDonor().getAvatar())
                        .build())
                .isAnonymous(donationEntity.isAnonymousDonation())
                .campaign(Campaign.builder()
                        .id(donationEntity.getCampaign().getId())
                        .totalAmountRaised(donationEntity.getCampaign().getDonations().stream()
                                .mapToLong( donation -> {
                                            if(donation.getState().equals(DonationState.ACCEPT)){
                                                return donation.getAmount();
                                            }
                                            return 0;
                                        }
                                )
                                .sum())
                        .totalNumberDonations(donationEntity.getCampaign().getDonations().stream()
                                .mapToLong( donation -> {
                                            if(donation.getState().equals(DonationState.ACCEPT)){
                                                return 1;
                                            }
                                            return 0;
                                        }
                                )
                                .sum())
                        .build())
                .build();
        messageService.newFailedDonations(response);
        return response;
    }

    public Page<Donation> getDonationOfCampaign(Integer page, Integer pageSize, String campaignId, String state, Authentication authentication){
        if(state == null || state.isEmpty() ){
            if(authentication == null)
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            if( !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
                throw new AppException(ErrorCode.UNAUTHORIZED);
            return getAllDonationOfCampaign(page, pageSize, campaignId);
        }
        if(DonationState.valueOf(state).equals(DonationState.FAILED) || DonationState.valueOf(state).equals(DonationState.FAILED)){
            if(authentication == null)
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            if( !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
                throw new AppException(ErrorCode.UNAUTHORIZED);
            return getDonationOfCampaignByState(page, pageSize, campaignId, DonationState.valueOf(state));
        }
        return  getDonationOfCampaignByState(page, pageSize, campaignId, DonationState.valueOf(state));

    }

    public Page<Donation> getDonationOfCampaignByState(Integer page, Integer pageSize, String campaignId, DonationState state){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<DonationEntity> donations = donationRepository.findByCampaignIdAndState(campaignId, state, pageable);
        return donations.map((donationEntity) -> Donation.builder()
                .id(donationEntity.getId())
                .amount(donationEntity.getAmount())
                .state(donationEntity.getState())
                .createdAt(donationEntity.getCreatedAt())
                .donor(donationEntity.isAnonymousDonation()?null: User.builder()
                        .id(donationEntity.getDonor().getId())
                        .email(donationEntity.getDonor().getEmail())
                        .avatar(donationEntity.getDonor().getAvatar())
                        .build())
                .isAnonymous(donationEntity.isAnonymousDonation())
                .campaign(Campaign.builder()
                        .id(donationEntity.getCampaign().getId())
                        .totalAmountRaised(donationEntity.getCampaign().getDonations().stream()
                                .mapToLong( donation -> {
                                            log.info("State: {}, equals {}",donation.getState(), donation.getState().equals(DonationState.ACCEPT));
                                            if(donation.getState().equals(DonationState.ACCEPT)){
                                                return donation.getAmount();
                                            }
                                            return 0;
                                        }
                                )
                                .sum())
                        .totalNumberDonations(donationEntity.getCampaign().getDonations().stream()
                                .mapToLong( donation -> {
                                            if(donation.getState().equals(DonationState.ACCEPT)){
                                                return 1;
                                            }
                                            return 0;
                                        }
                                )
                                .sum())
                        .build())
                .build());
    }

    public Page<Donation> getAllDonationOfCampaign( Integer page, Integer pageSize, String campaignId){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<DonationEntity> donations = donationRepository.findByCampaignId(campaignId, pageable);
        return donations.map((donationEntity) -> Donation.builder()
                .id(donationEntity.getId())
                .amount(donationEntity.getAmount())
                .state(donationEntity.getState())
                .createdAt(donationEntity.getCreatedAt())
                .donor(donationEntity.isAnonymousDonation()?null: User.builder()
                        .id(donationEntity.getDonor().getId())
                        .email(donationEntity.getDonor().getEmail())
                        .avatar(donationEntity.getDonor().getAvatar())
                        .build())
                .isAnonymous(donationEntity.isAnonymousDonation())
                .campaign(Campaign.builder()
                        .id(donationEntity.getCampaign().getId())
                        .totalAmountRaised(donationEntity.getCampaign().getDonations().stream()
                                .mapToLong( donation -> {
                                            if(donation.getState().equals(DonationState.ACCEPT)){
                                                return donation.getAmount();
                                            }
                                            return 0;
                                        }
                                )
                                .sum())
                        .totalNumberDonations(donationEntity.getCampaign().getDonations().stream()
                                .mapToLong( donation -> {
                                            if(donation.getState().equals(DonationState.ACCEPT)){
                                                return 1;
                                            }
                                            return 0;
                                        }
                                )
                                .sum())
                        .build())
                .build());
    }

    public Page<Donation> getAllDonationOfUser(Integer page, Integer pageSize, Long userId){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<DonationEntity> donations = donationRepository.findByDonorId(userId, pageable);
        return donations.map((donationEntity) -> Donation.builder()
                .id(donationEntity.getId())
                .amount(donationEntity.getAmount())
                .state(donationEntity.getState())
                .createdAt(donationEntity.getCreatedAt())
                .donor(donationEntity.isAnonymousDonation()?null: User.builder()
                        .id(donationEntity.getDonor().getId())
                        .email(donationEntity.getDonor().getEmail())
                        .avatar(donationEntity.getDonor().getAvatar())
                        .build())
                .isAnonymous(donationEntity.isAnonymousDonation())
                .campaign(Campaign.builder()
                        .id(donationEntity.getCampaign().getId())
                        .title(donationEntity.getCampaign().getTitle())
                        .build())
                .build());
    }
    public Page<Donation> getAll(Integer page, Integer pageSize){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<DonationEntity> donations = donationRepository.findAll( pageable);
        return donations.map((donationEntity) -> Donation.builder()
                .id(donationEntity.getId())
                .amount(donationEntity.getAmount())
                .state(donationEntity.getState())
                .createdAt(donationEntity.getCreatedAt())
                .donor( User.builder()
                        .id(donationEntity.getDonor().getId())
                        .email(donationEntity.getDonor().getEmail())
                        .avatar(donationEntity.getDonor().getAvatar())
                        .build())
                .isAnonymous(donationEntity.isAnonymousDonation())
                .campaign(Campaign.builder()
                        .id(donationEntity.getCampaign().getId())
                        .title(donationEntity.getCampaign().getTitle())
                        .build())
                .build());
    }

    public DonationStatistic statistic(){
        Pageable pageable = PageRequest.of(0, 10);
        return DonationStatistic.builder()
                .totalAcceptDonation(donationRepository.countDonationsByState(DonationState.ACCEPT))
                .totalAmountRaised(donationRepository.getTotalAmountRaised(DonationState.ACCEPT))
                .totalAmountDelivered(donationRepository.getTotalAmountDelivered())
                .latestDonations(donationRepository.findLatestDonations(pageable)
                        .stream().map(donationEntity -> Donation.builder()
                                .id(donationEntity.getId())

                                .amount(donationEntity.getAmount())
                                .campaign(Campaign.builder()
                                        .id(donationEntity.getCampaign().getId())
                                        .title(donationEntity.getCampaign().getTitle())
                                        .build())
                                .isAnonymous(donationEntity.isAnonymousDonation())
                                .donor(donationEntity.isAnonymousDonation()?null:User.builder()
                                        .id(donationEntity.getDonor().getId())
                                        .email(donationEntity.getDonor().getEmail())
                                        .build())
                                .build())
                        .toList()
                )
                .build();
    }
}
