package dev.ptit.charitymanagement.service.websocket;

import dev.ptit.charitymanagement.dtos.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class CampaignMessageService {
    SimpMessagingTemplate messagingTemplate;

//    public void adminNewCampaign(Campaign campaign){
//        messagingTemplate.convertAndSend("/topic/admin/campaigns",
//                campaign);
//    }

    public void newStartedCampaign(Campaign campaign){
//        khi mà có một campaign bắt từ 1 trạng thái, scheduled, created, stopped khác chuyển qua started
        messagingTemplate.convertAndSend("/topic/campaigns/STARTED",
                SimpleMessage.builder()
                        .action(SimpleMessageAction.NEW_CAMPAIGN)
                        .data(campaign)
                        .build());
        log.info("Send successfull to /topic/campaigns/STARTED");
    }

    public void newScheduledCampaign(Campaign campaign){
        messagingTemplate.convertAndSend("/topic/campaigns/SCHEDULED",
                SimpleMessage.builder()
                        .action(SimpleMessageAction.NEW_CAMPAIGN)
                        .data(campaign)
                        .build());
        log.info("Send successfull to /topic/campaigns/SCHEDULED");
    }
    public void newStoppedCampaign(Campaign campaign){
        log.info("New Stopped Campaign {}", campaign.getId());
        messagingTemplate.convertAndSend("/topic/campaigns/STOPPED",
                SimpleMessage.builder()
                        .action(SimpleMessageAction.NEW_CAMPAIGN)
                        .data(campaign)
                        .build());
        log.info("Send successfull to /topic/campaigns/STOPPED");
    }
    public void newEndedCampaign(Campaign campaign){
        messagingTemplate.convertAndSend("/topic/campaigns/ENDED",   SimpleMessage.builder()
                .action(SimpleMessageAction.NEW_CAMPAIGN)
                .data(campaign)
                .build());
        log.info("Send successfull to /topic/campaigns/ENDED");
    }

    public void newUpdateStatus(Campaign campaign){

        messagingTemplate.convertAndSend("/topic/campaigns/"+ campaign.getId(),  SimpleMessage.builder()
                .action(SimpleMessageAction.NEW_STATUS)
                .data(Campaign.builder()
                        .currentStatus(campaign.getCurrentStatus())
                        .result(campaign.getResult())
                        .build())
                .build());
        log.info("SEND UPDATE STATUS Successful");
    }

    public void newUpdatedDetail(Campaign campaign){
//        Cập nhật 1 thông tin nào đấy cua campaign => gửi thông tin được thay đổi đến topic này
//       topic/campaign/{id}
        messagingTemplate.convertAndSend("/topic/campaigns/"+ campaign.getId(),   SimpleMessage.builder()
                .action(SimpleMessageAction.NEW_DETAIL)
                .data(campaign)
                .build());
        log.info("SEND UPDATE detail Successful");
    }

    public void newAcceptDonations(Donation donation){
        //topic/campaign/{cpid}/donations/ACCEPT
        //theo dõi cấc khoản quyên góp có trạng thái accept mới
        messagingTemplate.convertAndSend("/topic/campaigns/"+ donation.getCampaign().getId()+"/donations/ACCEPT",  SimpleMessage.builder()
                .action(SimpleMessageAction.NEW_DONATION)
                .data(donation)
                .build());
        log.info("SEND DONATION Successful {}", "/topic/campaigns/"+ donation.getCampaign().getId() + "/donations/ACCEPT");
    }

    public void newPendingDonations(Donation donation){
        messagingTemplate.convertAndSend("/topic/campaigns/"+ donation.getCampaign().getId()+"/donations/PENDING",  SimpleMessage.builder()
                .action(SimpleMessageAction.NEW_DONATION)
                .data(donation)
                .build());
        log.info("SEND DONATION Successful {}", "/topic/campaigns/"+ donation.getCampaign().getId() + "/donations/PENDING");
    }
    public void newFailedDonations(Donation donation){
        messagingTemplate.convertAndSend("/topic/campaigns/"+ donation.getCampaign().getId()+"/donations/FAILED",  SimpleMessage.builder()
                .action(SimpleMessageAction.NEW_DONATION)
                .data(donation)
                .build());
        log.info("SEND DONATION Successful {}", "/topic/campaigns/"+ donation.getCampaign().getId() + "/donations/FAILED");
    }

    public void newComment(Comment comment){
        messagingTemplate.convertAndSend("/topic/campaigns/"+ comment.getCampaign().getId()+"/comments",  SimpleMessage.builder()
                .action(SimpleMessageAction.NEW_COMMENT)
                .data(comment)
                .build());
        log.info("SEND new Comment Successful {}", "/topic/campaigns/"+ comment.getCampaign().getId() + "/comments");
    }
    public void updateComment(Comment comment){
        messagingTemplate.convertAndSend("/topic/campaigns/"+ comment.getCampaign().getId()+"/comments",  SimpleMessage.builder()
                .action(SimpleMessageAction.UPDATE_COMMENT)
                .data(comment)
                .build());
        log.info("SEND update Comment Successful {}", "/topic/campaigns/"+ comment.getCampaign().getId() + "/comments");
    }
    public void deleteComment(Comment comment){
        messagingTemplate.convertAndSend("/topic/campaigns/"+ comment.getCampaign().getId()+"/comments",  SimpleMessage.builder()
                .action(SimpleMessageAction.DELETE_COMMENT)
                .data(comment)
                .build());
        log.info("SEND delete Comment Successful {}", "/topic/campaigns/"+ comment.getCampaign().getId() + "/comments");
    }



}
