package dev.ptit.charitymanagement.service.resultImage;

import dev.ptit.charitymanagement.dtos.Campaign;
import dev.ptit.charitymanagement.dtos.CampaignResult;
import dev.ptit.charitymanagement.dtos.Image;
import dev.ptit.charitymanagement.entity.CampaignEntity;
import dev.ptit.charitymanagement.entity.CampaignImageEntity;
import dev.ptit.charitymanagement.entity.CampaignResultEntity;
import dev.ptit.charitymanagement.entity.ResultImageEntity;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.CampaignImageRepository;
import dev.ptit.charitymanagement.repository.CampaignRepository;
import dev.ptit.charitymanagement.repository.CampaignResultRepository;
import dev.ptit.charitymanagement.repository.ResultImageRepository;
import dev.ptit.charitymanagement.service.image.S3CloudService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultImageService {
    ResultImageRepository resultImageRepository;
    S3CloudService s3CloudService;
    CampaignResultRepository campaignResultRepository;

    public Image create(Image image){
        String id = UUID.randomUUID().toString();
        String folder = "result-images-" + id;
        String key = folder+"/" + image.getFileName();
        String preSignedUrl = s3CloudService.generatePreSignedUrl(key);

        ResultImageEntity resultImageEntity = ResultImageEntity.builder()
                .id(id)
                .url(s3CloudService.generateFileLink(key))
                .fileName(image.getFileName())
                .description(image.getDescriptions())
                .build();
        resultImageEntity = resultImageRepository.save(resultImageEntity);
        return Image.builder()
                .id(resultImageEntity.getId())
                .fileName(resultImageEntity.getFileName())
                .folder(resultImageEntity.getFolder())
                .url(resultImageEntity.getUrl())
                .preSignedUrl(preSignedUrl)
                .build();
    }


    public Image addToResult(String id, CampaignResult result){
        CampaignResultEntity resultEntity = campaignResultRepository.findById(result.getId()).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_RESULT_NOT_EXISTED));
        ResultImageEntity resultImageEntity = resultImageRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESULT_IMAGE_NOT_EXISTED));

        resultImageEntity.setResult(resultEntity);
        resultImageEntity = resultImageRepository.save(resultImageEntity);


        return Image.builder()
                .id(resultImageEntity.getId())
                .fileName(resultImageEntity.getFileName())
                .url(resultImageEntity.getUrl())
                .descriptions(resultImageEntity.getDescription())
                .build();
    }

    public void deleteById(String id, Authentication authentication){
        ResultImageEntity resultImageEntity = resultImageRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESULT_IMAGE_NOT_EXISTED));
        if(resultImageEntity.getResult() == null){
            resultImageRepository.deleteById(id);
            s3CloudService.deleteFile(resultImageEntity.getFolder() +"/" + resultImageEntity.getFileName());
        }
        else{
            //image added campaigns
            if(authentication == null) throw  new AppException(ErrorCode.UNAUTHENTICATED);
            if( !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) throw new AppException(ErrorCode.UNAUTHORIZED);
            resultImageRepository.deleteById(id);
            s3CloudService.deleteFile(resultImageEntity.getFolder() +"/" + resultImageEntity.getFileName());
        }
    }
}
