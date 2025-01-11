package dev.ptit.charitymanagement.service.campaigns;

import dev.ptit.charitymanagement.dtos.APIResponse;
import dev.ptit.charitymanagement.dtos.Campaign;
import dev.ptit.charitymanagement.entity.CampaignEntity;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import dev.ptit.charitymanagement.repository.CampaignRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CampaignScheduler {

    CampaignService campaignService;
    CampaignRepository campaignRepository;
    SimpMessagingTemplate messagingTemplate;

    @Scheduled(cron = "0 * * * * ?")
    public void startScheduled() {
        log.info("LocalDate {}, localTime {}", LocalDate.now(), LocalTime.now());
        List<CampaignEntity> campaigns = campaignRepository.findByCurrentStatusAndStartDateAndStartTime(CampaignStatus.SCHEDULED, LocalDate.now(), LocalTime.now().withSecond(0).withNano(0));
        for(CampaignEntity campaign: campaigns){
            log.info("Start Campaign {} at {}", campaign.getTitle(), LocalDateTime.now().withSecond(0).withNano(0));
            Campaign response = campaignService.start(campaign.getId(), Campaign.builder()
                            .currentStatus(CampaignStatus.STARTED)
                            .endDate(campaign.getEndDate())
                            .endTime(campaign.getEndTime())
                    .build());

        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void stopCampaign() {
        log.info("Running at {}", LocalDateTime.now());
        List<CampaignEntity> campaigns = campaignRepository.findByCurrentStatusAndEndDateAndEndTime(CampaignStatus.STARTED, LocalDate.now(), LocalTime.now().withSecond(0).withNano(0));
        for(CampaignEntity campaign: campaigns){
            log.info("Stop Campaign {} at {}", campaign.getTitle(), LocalDateTime.now());
            Campaign response = campaignService.stop(campaign.getId());

        }
    }

}
