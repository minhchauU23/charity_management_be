package dev.ptit.charitymanagement.service.campaigns;

import dev.ptit.charitymanagement.dtos.request.campaign.CampaignCreationRequest;
import dev.ptit.charitymanagement.dtos.request.campaign.CampaignStatusRequest;
import dev.ptit.charitymanagement.dtos.request.campaign.CampaignUpdateRequest;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignHistoryDTO;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignDTO;
import dev.ptit.charitymanagement.dtos.response.user.UserDTO;
import dev.ptit.charitymanagement.entity.*;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.CampaignRepository;
import dev.ptit.charitymanagement.repository.NotificationTokenRepository;
import dev.ptit.charitymanagement.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignService {
    NotificationService notificationService;
    NotificationTokenRepository notificationTokenRepository;
    CampaignRepository campaignRepository;

    public Page<CampaignDTO> findAllByCurrentStatus(Integer page, Integer pageSize, String searchKeyWord, String sortRaw,String status){
        CampaignStatus currentStatus = CampaignStatus.fromString(status);
        String[] sortToArr = sortRaw.split(",");
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortToArr[1].equals("asc")? Sort.Direction.ASC: Sort.Direction.DESC, sortToArr[0]));
        log.info("Status {}", currentStatus.name());
        Page<Campaign> campaigns = campaignRepository.findByCurrentStatus(currentStatus, pageable);
        return campaigns.map(campaign -> CampaignDTO.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .shortDescription(campaign.getShortDescription())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .currentStatus(campaign.getCurrentStatus())
                .creator(UserDTO.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .build());

    }

    public CampaignDTO findById(String id){
        Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));
        return CampaignDTO.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .shortDescription(campaign.getShortDescription())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .currentStatus(campaign.getCurrentStatus())
                .creator(UserDTO.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .build();
    }

    public CampaignDTO create(CampaignCreationRequest request, User user){
        Campaign campaign = Campaign.builder()
                .content(request.getContent())
                .title(request.getTitle())
                .images(request.getImages())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .currentStatus(CampaignStatus.CREATED)
                .shortDescription(request.getShortDescription())
                .fundraisingGoal(request.getFundraisingGoal())
                .creator(user)
                .build();


        campaign = campaignRepository.save(campaign);
        return CampaignDTO.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .shortDescription(campaign.getShortDescription())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .currentStatus(campaign.getCurrentStatus())
                .creator(UserDTO.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .build();
    }

    public CampaignDTO updateDetail(String id, CampaignUpdateRequest request, User user){
        Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));
        campaign.setContent(request.getContent());
        campaign.setImages(request.getImages());
        campaign.setTitle(request.getTitle());
        campaign.setShortDescription(request.getShortDescription());
        campaign.setFundraisingGoal(request.getFundraisingGoal());
        campaign = campaignRepository.save(campaign);
        return CampaignDTO.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .shortDescription(campaign.getShortDescription())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .currentStatus(campaign.getCurrentStatus())
                .creator(UserDTO.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .build();
    }

    @Transactional
    public CampaignDTO updateStatus(String id, CampaignStatusRequest request){
        Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));
        if(!canChangeStatus(campaign.getCurrentStatus(), request.getStatus())){
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID_ORDER);
        }

        if(request.getStatus().equals(CampaignStatus.SCHEDULED)){
            if(request.getStartTime().isAfter(request.getEndTime())){
                throw new AppException(ErrorCode.BAD_REQUEST);
            }
            campaign.setStartTime(request.getStartTime());
            campaign.setEndTime(request.getEndTime());
        }
        campaign.setCurrentStatus(request.getStatus());

        campaign = campaignRepository.save(campaign);
        return CampaignDTO.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .shortDescription(campaign.getShortDescription())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .currentStatus(campaign.getCurrentStatus())
                .creator(UserDTO.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .build();
    }

    public CampaignDTO addResult(String id, CampaignResult campaignResult){
        return  null;
    }

    private boolean canChangeStatus(CampaignStatus currentStatus, CampaignStatus newStatus){
        switch (currentStatus){
            case CREATED:
                return newStatus == CampaignStatus.SCHEDULED || newStatus == CampaignStatus.REJECTED;
            case SCHEDULED:
                return newStatus == CampaignStatus.STARTED;
            case STARTED:
                return newStatus == CampaignStatus.STOPPED || newStatus == CampaignStatus.ENDED;
            case STOPPED :
                return newStatus == CampaignStatus.STARTED;
            case ENDED :
            case REJECTED:
                return false;
            default:
                return false;
        }
    }

}
