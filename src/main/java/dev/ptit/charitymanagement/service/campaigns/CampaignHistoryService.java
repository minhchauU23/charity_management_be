package dev.ptit.charitymanagement.service.campaigns;

import dev.ptit.charitymanagement.entity.Campaign;
import dev.ptit.charitymanagement.entity.CampaignHistory;
import dev.ptit.charitymanagement.entity.CampaignStatus;
import dev.ptit.charitymanagement.repository.CampaignHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CampaignHistoryService {
    CampaignHistoryRepository campaignHistoryRepository;

    private CampaignHistory getCurrentStatus(String campaignId){
        return campaignHistoryRepository.findFirstByCampaignIdOrderByUpdateAtDesc(campaignId).orElseThrow(() -> new RuntimeException("Campaign not exist"));
    }

    private boolean canChangeStatus(CampaignStatus currentStatus, CampaignStatus newStatus){
        switch (currentStatus){
            case PENDING :
                return newStatus == CampaignStatus.RUNNING || newStatus == CampaignStatus.REJECT;
            case RUNNING :
                return newStatus == CampaignStatus.STOPPED || newStatus == CampaignStatus.ENDED;

            case STOPPED :
                return newStatus == CampaignStatus.RUNNING;

            case ENDED :
            case REJECT:
                return false;
            default:
                throw new RuntimeException("INVALID STATUS");
        }
    }

    public CampaignHistory updateHistory(CampaignHistory campaignHistory){
        CampaignHistory currentStatus = getCurrentStatus(campaignHistory.getCampaign().getId());
        if(!canChangeStatus(currentStatus.getStatus(), campaignHistory.getStatus())){
            throw new RuntimeException("Status order invalid");
        }

        return campaignHistoryRepository.save(campaignHistory);

    }



}
