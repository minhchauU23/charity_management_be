package dev.ptit.charitymanagement.service.campaignImage;

import dev.ptit.charitymanagement.dtos.Campaign;
import dev.ptit.charitymanagement.dtos.Image;
import dev.ptit.charitymanagement.entity.CampaignEntity;
import dev.ptit.charitymanagement.entity.CampaignImageEntity;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.CampaignImageRepository;
import dev.ptit.charitymanagement.repository.CampaignRepository;
import dev.ptit.charitymanagement.service.image.S3CloudService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignImageService {
    CampaignImageRepository campaignImageRepository;
    CampaignRepository campaignRepository;
    S3CloudService s3CloudService;

    public Image create(Image image){
        String id = UUID.randomUUID().toString();
        String folder = "campaign-images-" + id;
        String key = folder+"/" + image.getFileName();
        String preSignedUrl = s3CloudService.generatePreSignedUrl(key);

        CampaignImageEntity campaignImageEntity = CampaignImageEntity.builder()
                .id(id)
                .url(s3CloudService.generateFileLink(key))
                .folder(folder)
                .fileName(image.getFileName())
                .description(image.getDescriptions())
                .build();
        campaignImageEntity = campaignImageRepository.save(campaignImageEntity);
        return Image.builder()
                .id(campaignImageEntity.getId())
                .fileName(campaignImageEntity.getFileName())
                .folder(campaignImageEntity.getFolder())
                .url(campaignImageEntity.getUrl())
                .preSignedUrl(preSignedUrl)
                .build();
    }


    public Image addToCampaign(String id, Campaign campaign){
        CampaignEntity campaignEntity = campaignRepository.findById(campaign.getId()).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));
        CampaignImageEntity campaignImageEntity = campaignImageRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_IMAGE_NOT_EXISTED));


        campaignImageEntity.setCampaign(campaignEntity);
        campaignImageEntity =  campaignImageRepository.save(campaignImageEntity);

        return Image.builder()
                .id(campaignImageEntity.getId())
                .fileName(campaignImageEntity.getFileName())
                .url(campaignImageEntity.getUrl())
                .descriptions(campaignImageEntity.getDescription())
                .build();
    }

    public void deleteById(String id, Authentication authentication){
        CampaignImageEntity campaignImageEntity = campaignImageRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_IMAGE_NOT_EXISTED));
        if(campaignImageEntity.getCampaign() == null){
            campaignImageRepository.deleteById(id);
            s3CloudService.deleteFile(campaignImageEntity.getFolder() +"/" + campaignImageEntity.getFileName());
        }
        else{
            //image added campaigns
            if(authentication == null) throw  new AppException(ErrorCode.UNAUTHENTICATED);
            if( !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) throw new AppException(ErrorCode.UNAUTHORIZED);
            campaignImageRepository.deleteById(id);
            s3CloudService.deleteFile(campaignImageEntity.getFolder() +"/" + campaignImageEntity.getFileName());
        }
    }
}
