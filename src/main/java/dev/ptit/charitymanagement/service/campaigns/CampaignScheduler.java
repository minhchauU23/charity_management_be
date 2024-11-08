package dev.ptit.charitymanagement.service.campaigns;

import dev.ptit.charitymanagement.dtos.request.campaign.CampaignStatusRequest;
import dev.ptit.charitymanagement.entity.Campaign;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import dev.ptit.charitymanagement.repository.CampaignRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CampaignScheduler {

    CampaignService campaignService;
    CampaignRepository campaignRepository;

    @Scheduled(cron = "0 * * * * ?")
    public void startScheduled() {
        List<Campaign> campaigns = campaignRepository.findByCurrentStatusAndStartTime(CampaignStatus.SCHEDULED, LocalDateTime.now().withSecond(0).withNano(0));
        for(Campaign campaign: campaigns){
            log.info("Start Campaign {} at {}", campaign.getTitle(), LocalDateTime.now().withSecond(0).withNano(0));
            campaignService.updateStatus(campaign.getId(), CampaignStatusRequest.builder()
                            .status(CampaignStatus.STARTED)
                    .build());
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void stopCampaign() {
        log.info("Running at {}", LocalDateTime.now());
        List<Campaign> campaigns = campaignRepository.findByCurrentStatusAndEndTime(CampaignStatus.STARTED, LocalDateTime.now().withSecond(0).withNano(0));
        for(Campaign campaign: campaigns){
            log.info("Stop Campaign {} at {}", campaign.getTitle(), LocalDateTime.now());
            campaignService.updateStatus(campaign.getId(), CampaignStatusRequest.builder()
                    .status(CampaignStatus.ENDED)
                    .build());
        }
    }

//    @Scheduled(cron = "0 * * * * ?")
//    public void rejectCampaign() {
//        log.info("Running at {}", LocalDateTime.now());
//        List<Campaign> campaigns = campaignRepository.findByCurrentStatusAndStartTime(CampaignStatus.SCHEDULED, LocalDateTime.now().withSecond(0).withNano(0));
//        for(Campaign campaign: campaigns){
//            log.info("Stop Campaign {} at {}", campaign.getTitle(), LocalDateTime.now());
//            campaignService.updateStatus(campaign.getId(), CampaignStatusRequest.builder()
//                    .status(CampaignStatus.ENDED)
//                    .build());
//        }
//    }
}
