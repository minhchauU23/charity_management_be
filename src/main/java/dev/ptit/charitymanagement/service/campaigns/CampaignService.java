package dev.ptit.charitymanagement.service.campaigns;

import dev.ptit.charitymanagement.dtos.*;

import dev.ptit.charitymanagement.entity.*;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.*;
import dev.ptit.charitymanagement.service.campaignImage.CampaignImageService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignService {
    CampaignCategoryRepository campaignCategoryRepository;
    CampaignRepository campaignRepository;
    CampaignImageService campaignImageService;
    CampaignMessageService campaignMessageService;



    public Page<Campaign> getAll(Integer page, Integer pageSize, String state, Long categoryId, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<CampaignEntity> campaigns;
        if(state != null && categoryId != null){
           return getAllByCurrentStatusAndCategoryId(page, pageSize, state, categoryId, authentication);
        }
        else if (state != null) {
          return getAllByCurrentStatus(page, pageSize, state, authentication);
        }
        else if(categoryId != null) {
           return getAllByCategoryId(page, pageSize, categoryId, authentication);
        }
        campaigns = campaignRepository.findAll(pageable);
        return campaigns.map(campaign -> Campaign.builder()
                .id(campaign.getId())
                .images(campaign.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .updateAt(campaign.getUpdateAt())
                .shortDescription(campaign.getShortDescription())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .currentStatus(campaign.getCurrentStatus())
                .category(
                        CampaignCategory.builder()
                                .id(campaign.getCategory().getId())
                                .name(campaign.getCategory().getName())
                                .build()
                )
                .creator(User.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .avatar(campaign.getCreator().getAvatar())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaign.getTotalAmountRaised())
                .totalNumberDonations(campaign.getTotalNumberDonations())
                .build());

    }

    public Page<Campaign> getAllByCurrentStatus(Integer page, Integer pageSize, String state, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        CampaignStatus campaignStatus = CampaignStatus.valueOf(state);
        if( (campaignStatus.equals(CampaignStatus.CREATED) || campaignStatus.equals(CampaignStatus.REJECTED)) &&
                (authentication == null || !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))){
            throw  new AppException(ErrorCode.UNAUTHORIZED);
        }
        Page<CampaignEntity>  campaigns = campaignRepository.findByCurrentStatus(campaignStatus, pageable);
        return campaigns.map(campaign -> Campaign.builder()
                .id(campaign.getId())
                .images(campaign.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .updateAt(campaign.getUpdateAt())
                .shortDescription(campaign.getShortDescription())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .currentStatus(campaign.getCurrentStatus())
                .category(
                        CampaignCategory.builder()
                                .id(campaign.getCategory().getId())
                                .name(campaign.getCategory().getName())
                                .build()
                )
                .creator(User.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .avatar(campaign.getCreator().getAvatar())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaign.getTotalAmountRaised())
                .totalNumberDonations(campaign.getTotalNumberDonations())
                .build());

    }

    public Page<Campaign> getAllByCurrentStatusAndCategoryId(Integer page, Integer pageSize, String state, Long categoryId, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        log.info("Call at get by currentStatus and category id");
        Page<CampaignEntity> campaigns;
        CampaignStatus campaignStatus = CampaignStatus.valueOf(state);
        if( (campaignStatus.equals(CampaignStatus.CREATED) || campaignStatus.equals(CampaignStatus.REJECTED)) &&
                (authentication == null || !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))){
            throw  new AppException(ErrorCode.UNAUTHORIZED);
        }

        boolean isExistCategory = campaignCategoryRepository.existsById(categoryId);
        if(!isExistCategory){
            throw  new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        campaigns = campaignRepository.findByCurrentStatusAndCategoryId( campaignStatus, categoryId, pageable);
        log.info("Campaigns : {}", campaigns.stream().count());
        for(CampaignEntity cp : campaigns){
            log.info("Campaign: {}, {}, {}", cp.getTitle(), cp.getTotalAmountRaised(), cp.getTotalNumberDonations());
        }
        return campaigns.map(campaign -> Campaign.builder()
                .id(campaign.getId())
                .images(campaign.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .updateAt(campaign.getUpdateAt())
                .shortDescription(campaign.getShortDescription())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .currentStatus(campaign.getCurrentStatus())
                .category(
                        CampaignCategory.builder()
                                .id(campaign.getCategory().getId())
                                .name(campaign.getCategory().getName())
                                .build()
                )
                .creator(User.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .avatar(campaign.getCreator().getAvatar())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaign.getTotalAmountRaised())
                .totalNumberDonations(campaign.getTotalNumberDonations())
                .build());

    }

    public Page<Campaign> search(Integer page, Integer pageSize, String keyword, List<CampaignStatus> statuses, Authentication authentication){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<CampaignEntity> campaigns;
        if(statuses.contains(CampaignStatus.CREATED )|| statuses.contains(CampaignStatus.REJECTED)){
            if(authentication == null ||!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

        }
        campaigns = campaignRepository.search( statuses,keyword,pageable  );
        return campaigns.map(campaign -> Campaign.builder()
                .id(campaign.getId())
                .images(campaign.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .updateAt(campaign.getUpdateAt())
                .shortDescription(campaign.getShortDescription())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .currentStatus(campaign.getCurrentStatus())
                .category(
                        CampaignCategory.builder()
                                .id(campaign.getCategory().getId())
                                .name(campaign.getCategory().getName())
                                .build()
                )
                .creator(User.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .avatar(campaign.getCreator().getAvatar())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaign.getDonations().stream()
                        .mapToLong( donation -> {
                                    log.info("State: {}, equals {}",donation.getState(), donation.getState().equals(DonationState.ACCEPT));
                                    if(donation.getState().equals(DonationState.ACCEPT)){
                                        return donation.getAmount();
                                    }
                                    return 0;
                                }
                        )
                        .sum())
                .totalNumberDonations(campaign.getDonations().stream()
                        .mapToLong( donation -> {
                                    if(donation.getState().equals(DonationState.ACCEPT)){
                                        return 1;
                                    }
                                    return 0;
                                }
                        )
                        .sum())
                .build());
    }

    public Page<Campaign> getAllByCategoryId(Integer page, Integer pageSize, Long categoryId, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        if((authentication == null || !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        boolean isExistCategory = campaignCategoryRepository.existsById(categoryId);
        if(!isExistCategory){
            throw  new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        Page<CampaignEntity> campaigns = campaignRepository.findByCategoryId(categoryId, pageable);
        return campaigns.map(campaign -> Campaign.builder()
                .id(campaign.getId())
                .images(campaign.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .updateAt(campaign.getUpdateAt())
                .shortDescription(campaign.getShortDescription())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .currentStatus(campaign.getCurrentStatus())
                .category(
                        CampaignCategory.builder()
                                .id(campaign.getCategory().getId())
                                .name(campaign.getCategory().getName())
                                .build()
                )
                .creator(User.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .avatar(campaign.getCreator().getAvatar())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaign.getTotalAmountRaised())
                .totalNumberDonations(campaign.getTotalNumberDonations())
                .build());

    }

    public Page<Campaign> getCampaignOfUser(Integer page, Integer pageSize, Long userId){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<CampaignEntity> campaigns = campaignRepository.findByUserId(userId, pageable);
        return campaigns.map(campaign -> Campaign.builder()
                .id(campaign.getId())
                .images(campaign.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .title(campaign.getTitle())
                .content(campaign.getContent())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .updateAt(campaign.getUpdateAt())
                .shortDescription(campaign.getShortDescription())
                .fundraisingGoal(campaign.getFundraisingGoal())
                .currentStatus(campaign.getCurrentStatus())
                .category(
                        CampaignCategory.builder()
                                .id(campaign.getCategory().getId())
                                .name(campaign.getCategory().getName())
                                .build()
                )
                .creator(User.builder()
                        .id(campaign.getCreator().getId())
                        .email(campaign.getCreator().getEmail())
                        .avatar(campaign.getCreator().getAvatar())
                        .firstName(campaign.getCreator().getFirstName())
                        .lastName(campaign.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaign.getTotalAmountRaised())
                .totalNumberDonations(campaign.getTotalNumberDonations())
                .build());
    }

    public Campaign getById(String id, Authentication authentication){
        CampaignEntity campaignEntity = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));
        log.info("startDate: {}",campaignEntity.getStartDate());
        log.info("end date: {}", campaignEntity.getEndDate());

        log.info("In get by id {}", (authentication == null || !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))));
//        log.info("In get by id role {}", !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
        if((campaignEntity.getCurrentStatus().equals(CampaignStatus.CREATED) || campaignEntity.getCurrentStatus().equals(CampaignStatus.REJECTED))){
            if(authentication == null) throw  new AppException(ErrorCode.UNAUTHENTICATED);
            if( !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return Campaign.builder()
                .id(campaignEntity.getId())
                .images(campaignEntity.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .title(campaignEntity.getTitle())
                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(campaignEntity.getStartDate())
                .endDate(campaignEntity.getEndDate())
                .startTime(campaignEntity.getStartTime())
                .endTime(campaignEntity.getEndTime())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(
                        CampaignCategory.builder()
                                .id(campaignEntity.getCategory().getId())
                                .name(campaignEntity.getCategory().getName())
                                .build()
                )
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .email(campaignEntity.getCreator().getEmail())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaignEntity.getTotalAmountRaised())
                .totalNumberDonations(campaignEntity.getTotalNumberDonations())
                .build();
    }
    @Transactional
    public Campaign create(Campaign request, UserEntity user){
        log.info("Start Date: {}; endDATe: {}; now: {}", request.getStartDate(), request.getEndDate(), LocalDate.now());
        log.info("Start Date: {}; endDATe: {}; now: {}", !request.getStartDate().isAfter(LocalDate.now()), !request.getEndDate().isAfter(LocalDate.now()), request.getStartDate().isAfter(request.getEndDate()));
        if(LocalDate.now().isAfter(request.getStartDate())){
            throw  new AppException(ErrorCode.CAMPAIGN_START_DATE_INVALID);
        }
        if(LocalDate.now().isAfter(request.getEndDate())){
            throw  new AppException(ErrorCode.CAMPAIGN_END_DATE_INVALID);
        }
        if(request.getStartDate().isAfter(request.getEndDate())){
            throw  new AppException(ErrorCode.CAMPAIGN_START_DATE_INVALID);
        }

        CampaignEntity campaignEntity = CampaignEntity.builder()
                .content(request.getContent())
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .createdAt(LocalDate.now())
                .endDate(request.getEndDate())
                .currentStatus(CampaignStatus.CREATED)
                .shortDescription(request.getShortDescription())
                .fundraisingGoal(request.getFundraisingGoal())
                .creator(user)
                .category(CampaignCategoryEntity.builder()
                        .id(request.getCategory().getId())
                        .build())
                .build();
        campaignEntity = campaignRepository.save(campaignEntity);

        Campaign response =  Campaign.builder()
                .id(campaignEntity.getId())
                .title(campaignEntity.getTitle())
                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(CampaignCategory.builder()
                        .id(campaignEntity.getCategory().getId())
                        .name(campaignEntity.getCategory().getName())
                        .build())
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .email(campaignEntity.getCreator().getEmail())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .build();
        List<Image> images = new ArrayList<>();
        for(Image imageRequest : request.getImages()){
            Image image = campaignImageService.addToCampaign(imageRequest.getId(), response);
            images.add(image);
        }
        response.setImages(images);
        return response;
    }

    public Campaign updateDetail(String id, Campaign campaign, UserEntity user){

        CampaignEntity campaignEntity = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));

        campaignEntity.setContent(campaign.getContent());
        campaignEntity.setTitle(campaign.getTitle());
        campaignEntity.setShortDescription(campaign.getShortDescription());
        campaignEntity.setFundraisingGoal(campaign.getFundraisingGoal());
        campaignEntity.setUpdateAt(LocalDate.now());

        if(campaignEntity.getCurrentStatus().equals(CampaignStatus.CREATED) || campaignEntity.getCurrentStatus().equals(CampaignStatus.SCHEDULED)){
            campaignEntity.setStartDate(campaign.getStartDate());
            campaignEntity.setStartTime(campaign.getStartTime());
        }
        if(campaignEntity.getCurrentStatus().equals(CampaignStatus.CREATED) || campaignEntity.getCurrentStatus().equals(CampaignStatus.SCHEDULED) ||
                campaignEntity.getCurrentStatus().equals(CampaignStatus.STARTED)){
            campaignEntity.setEndDate(campaign.getEndDate());
            campaignEntity.setEndTime(campaign.getEndTime());
        }

        campaignEntity = campaignRepository.save(campaignEntity);
        Campaign response = Campaign.builder()
                .id(campaignEntity.getId())
                .title(campaignEntity.getTitle())

                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(campaignEntity.getStartDate())
                .endDate(campaignEntity.getEndDate())
                .updateAt(campaignEntity.getUpdateAt())
                .startTime(campaignEntity.getStartTime())
                .endTime(campaignEntity.getEndTime())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(CampaignCategory.builder()
                        .id(campaignEntity.getCategory().getId())
                        .name(campaignEntity.getCategory().getName())
                        .build())
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .email(campaignEntity.getCreator().getEmail())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaignEntity.getTotalAmountRaised())
                .totalNumberDonations(campaignEntity.getTotalNumberDonations())
                .build();
//        messagingTemplate.convertAndSend("/topic/campaigns/"+response.getId(), APIResponse.builder()
//                        .message("campaign")
//                        .data(response)
//                .build());
        List<Image> images = new ArrayList<>();
        for(Image imageRequest : campaign.getImages()){
            Image image = campaignImageService.addToCampaign(imageRequest.getId(), response);
            images.add(image);
        }
        response.setImages(images);
        campaignMessageService.newUpdatedDetail(response);
        return response;
    }

    @Transactional
    public Campaign schedule(String id, Campaign campaign){
        CampaignEntity campaignEntity = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));

        if(!canChangeStatus(campaignEntity.getCurrentStatus(), CampaignStatus.SCHEDULED)){
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID_ORDER);
        }
        //thiếu với ngày hiện tại
        if(campaign.getStartDate().isAfter(campaign.getEndDate())){
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if(campaign.getStartDate().equals(campaign.getEndDate()) && campaign.getStartTime().isAfter(campaign.getEndTime())){
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        campaignEntity.setStartDate(campaign.getStartDate());
        campaignEntity.setEndDate(campaign.getEndDate());
        campaignEntity.setStartTime(campaign.getStartTime());
        campaignEntity.setEndTime(campaign.getEndTime());

        campaignEntity.setCurrentStatus(CampaignStatus.SCHEDULED);
        campaignEntity.setUpdateAt(LocalDate.now());
        campaignEntity = campaignRepository.save(campaignEntity);
        Campaign response = Campaign.builder()
                .id(campaignEntity.getId())
                .title(campaignEntity.getTitle())
                .images(campaignEntity.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(campaignEntity.getStartDate())
                .endDate(campaignEntity.getEndDate())
                .startTime(campaignEntity.getStartTime())
                .endTime(campaignEntity.getEndTime())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(CampaignCategory.builder()
                        .id(campaignEntity.getCategory().getId())
                        .name(campaignEntity.getCategory().getName())
                        .build())
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .email(campaignEntity.getCreator().getEmail())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaignEntity.getTotalAmountRaised())
                .totalNumberDonations(campaignEntity.getTotalNumberDonations())
                .build();

        campaignMessageService.newScheduledCampaign(response);
        campaignMessageService.newUpdateStatus(response);
        return response;
    }

    @Transactional
    public Campaign reject(String id){
        CampaignEntity campaignEntity = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));

        if(!canChangeStatus(campaignEntity.getCurrentStatus(), CampaignStatus.REJECTED)){
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID_ORDER);
        }

        campaignEntity.setCurrentStatus(CampaignStatus.REJECTED);
        campaignEntity.setUpdateAt(LocalDate.now());
        campaignEntity = campaignRepository.save(campaignEntity);
        Campaign response = Campaign.builder()
                .id(campaignEntity.getId())
                .title(campaignEntity.getTitle())
                .images(campaignEntity.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(campaignEntity.getStartDate())
                .endDate(campaignEntity.getEndDate())
                .startTime(campaignEntity.getStartTime())
                .endTime(campaignEntity.getEndTime())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(CampaignCategory.builder()
                        .id(campaignEntity.getCategory().getId())
                        .name(campaignEntity.getCategory().getName())
                        .build())
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .email(campaignEntity.getCreator().getEmail())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaignEntity.getTotalAmountRaised())
                .totalNumberDonations(campaignEntity.getTotalNumberDonations())
                .build();

        return response;
    }

    @Transactional
    public Campaign start(String id, Campaign campaign){
        CampaignEntity campaignEntity = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));

        if(!canChangeStatus(campaignEntity.getCurrentStatus(), CampaignStatus.STARTED)){
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID_ORDER);
        }
        //thiếu với ngày hiện tại và endate time

        campaignEntity.setStartDate(LocalDate.now());
        campaignEntity.setEndDate(campaign.getEndDate());
        campaignEntity.setStartTime(LocalTime.now().withSecond(0).withNano(0));
        campaignEntity.setEndTime(campaign.getEndTime());

        campaignEntity.setCurrentStatus(CampaignStatus.STARTED);
        campaignEntity.setUpdateAt(LocalDate.now());
        campaignEntity = campaignRepository.save(campaignEntity);
        Campaign response = Campaign.builder()
                .id(campaignEntity.getId())
                .title(campaignEntity.getTitle())
                .images(campaignEntity.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(campaignEntity.getStartDate())
                .endDate(campaignEntity.getEndDate())
                .startTime(campaignEntity.getStartTime())
                .endTime(campaignEntity.getEndTime())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(CampaignCategory.builder()
                        .id(campaignEntity.getCategory().getId())
                        .name(campaignEntity.getCategory().getName())
                        .build())
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .email(campaignEntity.getCreator().getEmail())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaignEntity.getTotalAmountRaised())
                .totalNumberDonations(campaignEntity.getTotalNumberDonations())
                .build();
        //message service send update

        //
        campaignMessageService.newStartedCampaign(response);
        campaignMessageService.newUpdateStatus(response);
        return response;
    }

    @Transactional
    public Campaign stop(String id){
        CampaignEntity campaignEntity = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));

        if(!canChangeStatus(campaignEntity.getCurrentStatus(), CampaignStatus.STOPPED)){
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID_ORDER);
        }

        campaignEntity.setCurrentStatus(CampaignStatus.STOPPED);
        campaignEntity.setUpdateAt(LocalDate.now());
        campaignEntity = campaignRepository.save(campaignEntity);
        Campaign response = Campaign.builder()
                .id(campaignEntity.getId())
                .title(campaignEntity.getTitle())
                .images(campaignEntity.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(campaignEntity.getStartDate())
                .endDate(campaignEntity.getEndDate())
                .startTime(campaignEntity.getStartTime())
                .endTime(campaignEntity.getEndTime())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(CampaignCategory.builder()
                        .id(campaignEntity.getCategory().getId())
                        .name(campaignEntity.getCategory().getName())
                        .build())
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .email(campaignEntity.getCreator().getEmail())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaignEntity.getTotalAmountRaised())
                .totalNumberDonations(campaignEntity.getTotalNumberDonations())
                .build();
        //message service send update

        campaignMessageService.newStoppedCampaign(response);
        campaignMessageService.newUpdateStatus(response);
        return response;
    }
    @Transactional
    public Campaign restart(String id, Campaign campaign){
        CampaignEntity campaignEntity = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));

        if(!canChangeStatus(campaignEntity.getCurrentStatus(), CampaignStatus.STARTED)){
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID_ORDER);
        }
        //validate
        campaignEntity.setEndDate(campaign.getEndDate());
        campaignEntity.setEndTime(campaign.getEndTime());

        campaignEntity.setCurrentStatus(CampaignStatus.STARTED);
        campaignEntity.setUpdateAt(LocalDate.now());
        campaignEntity = campaignRepository.save(campaignEntity);
        Campaign response = Campaign.builder()
                .id(campaignEntity.getId())
                .title(campaignEntity.getTitle())
                .images(campaignEntity.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(campaignEntity.getStartDate())
                .endDate(campaignEntity.getEndDate())
                .startTime(campaignEntity.getStartTime())
                .endTime(campaignEntity.getEndTime())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(CampaignCategory.builder()
                        .id(campaignEntity.getCategory().getId())
                        .name(campaignEntity.getCategory().getName())
                        .build())
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .email(campaignEntity.getCreator().getEmail())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaignEntity.getTotalAmountRaised())
                .totalNumberDonations(campaignEntity.getTotalNumberDonations())
                .build();
        //message service send update

        campaignMessageService.newStartedCampaign(response);
        campaignMessageService.newUpdateStatus(response);
        return response;
    }

    @Transactional
    public Campaign result(String id, CampaignResult result){
        CampaignEntity campaignEntity = campaignRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));

        if(!canChangeStatus(campaignEntity.getCurrentStatus(), CampaignStatus.ENDED)){
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID_ORDER);
        }
        //validate
        CampaignResultEntity resultEntity = CampaignResultEntity.builder()
                .executeDate(result.getExecuteDate())
                .executeLocation(result.getExecuteLocation())
                .content(result.getContent())
                .campaign(campaignEntity)
                .build();


        campaignEntity.setCurrentStatus(CampaignStatus.ENDED);
        campaignEntity.setUpdateAt(LocalDate.now());
        campaignEntity.setResult(resultEntity);
        campaignEntity = campaignRepository.save(campaignEntity);
        Campaign response = Campaign.builder()
                .id(campaignEntity.getId())
                .title(campaignEntity.getTitle())
                .images(campaignEntity.getImages().stream().map(
                        img -> Image.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .descriptions(img.getDescription())
                                .build()
                ).toList())
                .content(campaignEntity.getContent())
                .fundraisingGoal(campaignEntity.getFundraisingGoal())
                .shortDescription(campaignEntity.getShortDescription())
                .startDate(campaignEntity.getStartDate())
                .endDate(campaignEntity.getEndDate())
                .startTime(campaignEntity.getStartTime())
                .endTime(campaignEntity.getEndTime())
                .currentStatus(campaignEntity.getCurrentStatus())
                .category(CampaignCategory.builder()
                        .id(campaignEntity.getCategory().getId())
                        .name(campaignEntity.getCategory().getName())
                        .build())
                .creator(User.builder()
                        .id(campaignEntity.getCreator().getId())
                        .avatar(campaignEntity.getCreator().getAvatar())
                        .email(campaignEntity.getCreator().getEmail())
                        .firstName(campaignEntity.getCreator().getFirstName())
                        .lastName(campaignEntity.getCreator().getLastName())
                        .build())
                .totalAmountRaised(campaignEntity.getTotalAmountRaised())
                .totalNumberDonations(campaignEntity.getTotalNumberDonations())
                .result(CampaignResult.builder()
                        .id(campaignEntity.getResult().getId())
                        .executeLocation(campaignEntity.getResult().getExecuteLocation())
                        .executeDate(campaignEntity.getResult().getExecuteDate())
                        .content(campaignEntity.getResult().getContent())
                        .build())
                .build();
        //message service send update

        campaignMessageService.newEndedCampaign(response);
        campaignMessageService.newUpdateStatus(response);
        return response;
    }

    private boolean canChangeStatus(CampaignStatus currentStatus, CampaignStatus newStatus){
        log.info("Current status: {} next status {}", currentStatus, newStatus );
        switch (currentStatus){
            case CREATED:
                return newStatus == CampaignStatus.SCHEDULED || newStatus == CampaignStatus.REJECTED;
            case SCHEDULED:
                return newStatus == CampaignStatus.STARTED;
            case STARTED:
                return newStatus == CampaignStatus.STOPPED ;
            case STOPPED :
                return newStatus == CampaignStatus.STARTED ||newStatus == CampaignStatus.ENDED;
            case ENDED :
            case REJECTED:
                return false;
            default:
                return false;
        }
    }

    @Transactional
    public CampaignStatistic getStatistic(){
        Long totalStartedCampaign = campaignRepository.countCampaignsByStatus(CampaignStatus.STARTED);
        Pageable pageable = PageRequest.of(0, 6);
        List<CampaignEntity> lastUpdateCampaign = campaignRepository.getLastUpdateCampaign(pageable);
        return CampaignStatistic.builder()
                .totalStartedCampaign(totalStartedCampaign)
                .latestUpdateCampaigns(lastUpdateCampaign.stream().map(cp ->
                    Campaign.builder()
                                    .id(cp.getId())
                                    .title(cp.getTitle())
                                    .currentStatus(cp.getCurrentStatus())
                                    .build()
                ).toList())
                .build();
    }

}
