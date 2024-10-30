package dev.ptit.charitymanagement.service.campaigns;

import com.google.api.Authentication;
import dev.ptit.charitymanagement.dtos.request.campaign.CampaignCreationRequest;
import dev.ptit.charitymanagement.dtos.request.notification.NotificationTokenRequest;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignHistoryResponse;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignResponse;
import dev.ptit.charitymanagement.entity.*;
import dev.ptit.charitymanagement.repository.CampaignRepository;
import dev.ptit.charitymanagement.repository.NotificationTokenRepository;
import dev.ptit.charitymanagement.service.CRUDService;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignService {
    NotificationService notificationService;
    NotificationTokenRepository notificationTokenRepository;
    CampaignRepository campaignRepository;
    public void test(){
        List<NotificationToken> tokens = notificationTokenRepository.findAll();
        for(NotificationToken token : tokens){
            Notification notification = new FirebaseNotification();
            notification.setTitle("Test");
            notification.setType("FIREBASE");
            notification.setContent("Test content");
            notification.setReceipt(token.getRegistration());
            notificationService.send(notification);
        }
    }

    public CampaignResponse create(CampaignCreationRequest request, User user){
        Campaign campaign = Campaign.builder()
                .content(request.getContent())
                .title(request.getTitle())
                .images(request.getImages())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        CampaignHistory campaignHistory = new CampaignHistory();
        campaignHistory.setCampaign(campaign);
        campaignHistory.setUpdateAt(new Date());
        campaignHistory.setStatus(CampaignStatus.PENDING);
        campaignHistory.setUser(user);
        campaign.setHistories(Set.of(campaignHistory));
        campaign = campaignRepository.save(campaign);
        return CampaignResponse.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .images(campaign.getImages())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .histories(campaign.getHistories().stream().map(cp -> CampaignHistoryResponse.builder()
                        .id(cp.getId())
                        .status(cp.getStatus())
                        .updateAt(cp.getUpdateAt())
                        .build()).collect(Collectors.toSet()))
                .build();
    }

    public CampaignResponse update(CampaignCreationRequest request){
        Campaign campaign = Campaign.builder()
                .content(request.getContent())
                .title(request.getTitle())
                .images(request.getImages())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        CampaignHistory campaignHistory = new CampaignHistory();
        campaignHistory.setCampaign(campaign);
        campaignHistory.setUpdateAt(new Date());
        campaignHistory.setStatus(CampaignStatus.PENDING);
        campaign.setHistories(Set.of(campaignHistory));
        campaign = campaignRepository.save(campaign);
        return CampaignResponse.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .images(campaign.getImages())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .histories(campaign.getHistories().stream().map(cp -> CampaignHistoryResponse.builder()
                        .id(cp.getId())
                        .status(cp.getStatus())
                        .updateAt(cp.getUpdateAt())
                        .build()).collect(Collectors.toSet()))
                .build();
    }



}
