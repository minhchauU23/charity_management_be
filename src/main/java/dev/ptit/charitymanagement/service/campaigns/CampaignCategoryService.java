package dev.ptit.charitymanagement.service.campaigns;

import dev.ptit.charitymanagement.dtos.CampaignCategory;
import dev.ptit.charitymanagement.entity.CampaignCategoryEntity;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.CampaignCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CampaignCategoryService {
    CampaignCategoryRepository campaignCategoryRepository;
    public CampaignCategory create(CampaignCategory campaignCategory){
        CampaignCategoryEntity categoryEntity = CampaignCategoryEntity.builder()
                .name(campaignCategory.getName())
                .build();
        categoryEntity = campaignCategoryRepository.save(categoryEntity);
        return CampaignCategory.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();
    }

    public CampaignCategory update(Long id, CampaignCategory campaignCategory){
        CampaignCategoryEntity categoryEntity = campaignCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        categoryEntity.setName(campaignCategory.getName());
        categoryEntity = campaignCategoryRepository.save(categoryEntity);
        return CampaignCategory.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();
    }

    public CampaignCategory findById(Long id){
        CampaignCategoryEntity category = campaignCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        return CampaignCategory.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Page<CampaignCategory> findAll(Integer page,Integer pageSize,String searchKeyWord, String sortRaw){
        String[] sortToArr = sortRaw.split(",");
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortToArr[1].equals("asc")? Sort.Direction.ASC: Sort.Direction.DESC, sortToArr[0]));
        Page<CampaignCategoryEntity> categories;

        if(searchKeyWord.trim().isEmpty()){
            categories = campaignCategoryRepository.findAll( pageable);
        }
        else {
            categories = campaignCategoryRepository.search( searchKeyWord.trim(), pageable);
        }
        return categories.map(category -> CampaignCategory.builder()
                .id(category.getId())
                .name(category.getName())
                .build());
    }
}
