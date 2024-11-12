package dev.ptit.charitymanagement.service.campaigns;

import dev.ptit.charitymanagement.dtos.request.campaign.CampaignCategoryRequest;
import dev.ptit.charitymanagement.dtos.response.campaign.CampaignCategoryDTO;
import dev.ptit.charitymanagement.entity.CampaignCategory;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.CampaignCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignCategoryService {
    CampaignCategoryRepository campaignCategoryRepository;
    public CampaignCategoryDTO create(CampaignCategoryRequest request){
        CampaignCategory category = CampaignCategory.builder()
                .name(request.getName())
                .build();
        category = campaignCategoryRepository.save(category);
        return CampaignCategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public CampaignCategoryDTO update(Long id, CampaignCategoryRequest request){
        CampaignCategory category = campaignCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        category.setName(request.getName());
        category = campaignCategoryRepository.save(category);
        return CampaignCategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public CampaignCategoryDTO findById(Long id){
        CampaignCategory category = campaignCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        return CampaignCategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public List<CampaignCategoryDTO> findAll(){
        List<CampaignCategory> categories = campaignCategoryRepository.findAll();
        return categories.stream().map(category -> CampaignCategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build()).toList();
    }
}
