package dev.ptit.charitymanagement.service.notificationTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ptit.charitymanagement.dtos.NotificationTemplate;
import dev.ptit.charitymanagement.entity.NotificationEntity;
import dev.ptit.charitymanagement.entity.NotificationTemplateEntity;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.NotificationTemplateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationTemplateService {
    NotificationTemplateRepository notificationTemplateRepository;
    ObjectMapper objectMapper;
    public NotificationTemplate create(NotificationTemplate notificationTemplate){
        NotificationTemplateEntity notificationTemplateEntity = NotificationTemplateEntity.builder()
                .content(notificationTemplate.getContent())
                .dataForm(notificationTemplate.getDataForm())
                .name(notificationTemplate.getName())
                .build();
        notificationTemplateEntity = notificationTemplateRepository.save(notificationTemplateEntity);
        return NotificationTemplate.builder()
                .id(notificationTemplateEntity.getId())
                .name(notificationTemplate.getName())
                .dataForm(notificationTemplate.getDataForm())
                .content(notificationTemplate.getContent())
                .build();
    }

    public Page<NotificationTemplate> getAll(Integer page, Integer pageSize){
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<NotificationTemplateEntity> templateEntities = notificationTemplateRepository.findAll(pageable);
        return templateEntities.map(template -> NotificationTemplate.builder()
                        .id(template.getId())
                .name(template.getName())
                .content(template.getContent())
                .dataForm(template.getDataForm())
                .build());
    }
    public NotificationTemplate getById(Long id){
        NotificationTemplateEntity templateEntity = notificationTemplateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TEMPLATE_NOT_EXISTED));

        return NotificationTemplate.builder()
                .id(templateEntity.getId())
                .name(templateEntity.getName())
                .content(templateEntity.getContent())
                .dataForm(templateEntity.getDataForm())
                .build();
    }

    public NotificationTemplate update(Long id, NotificationTemplate notificationTemplate){
        NotificationTemplateEntity templateEntity = notificationTemplateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TEMPLATE_NOT_EXISTED));
        log.info("Template before Saved: {}, {}, {}", notificationTemplate.getName(), notificationTemplate.getContent(), notificationTemplate.getDataForm());
        templateEntity.setContent(notificationTemplate.getContent());
        templateEntity.setName(notificationTemplate.getName());
        templateEntity.setDataForm(notificationTemplate.getDataForm());
        templateEntity = notificationTemplateRepository.save(templateEntity);
        log.info("Template saved: {}, {}, {}", templateEntity.getName(), templateEntity.getContent(), templateEntity.getDataForm());
        return NotificationTemplate.builder()
                .id(templateEntity.getId())
                .name(templateEntity.getName())
                .content(templateEntity.getContent())
                .dataForm(templateEntity.getDataForm())
                .build();
    }

    public String generateContent(String data, NotificationTemplateEntity template)  {

        try {
            Map<String, String> mapData = objectMapper.readValue(data, Map.class);
            String content = template.getContent();
            for(Map.Entry<String, String> entry: mapData.entrySet()){
                String placeholder = "{" + entry.getKey() + "}"; // Tìm {tên biến}
                if (content.contains(placeholder)) {
                    content = content.replace(placeholder, entry.getValue()); // Thay thế bằng giá trị
                }
            }
            log.info("Generated Content: {}", content);
            return content;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
