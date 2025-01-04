package dev.ptit.charitymanagement.service.comment;

import dev.ptit.charitymanagement.dtos.Campaign;
import dev.ptit.charitymanagement.dtos.Comment;
import dev.ptit.charitymanagement.dtos.User;
import dev.ptit.charitymanagement.entity.CampaignEntity;
import dev.ptit.charitymanagement.entity.CommentEntity;
import dev.ptit.charitymanagement.entity.DonationEntity;
import dev.ptit.charitymanagement.entity.UserEntity;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.CampaignRepository;
import dev.ptit.charitymanagement.repository.CommentRepository;
import dev.ptit.charitymanagement.service.websocket.CampaignMessageService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    CampaignRepository campaignRepository;
    CampaignMessageService messageService;
    public Comment createComment(Comment comment, UserEntity user){
        CampaignEntity campaignEntity = campaignRepository.findById(comment.getCampaign().getId()).orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_EXISTED));
        CommentEntity commentEntity = CommentEntity.builder()
                .campaign(campaignEntity)
                .user(user)
                .content(comment.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        commentEntity = commentRepository.save(commentEntity);
        Comment response =  Comment.builder()
                .id(commentEntity.getId())
                .createdAt(commentEntity.getCreatedAt())
                .content(commentEntity.getContent())
                .campaign(Campaign.builder()
                        .id(commentEntity.getCampaign().getId())
                        .build())
                .user(User.builder()
                        .id(commentEntity.getUser().getId())
                        .avatar(commentEntity.getUser().getAvatar())
                        .email(commentEntity.getUser().getEmail())
                        .build())
                .build();
        messageService.newComment(response);
        return response;
    }

    public Page<Comment> getByCampaignId( Integer page, Integer pageSize, String campaignId){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<CommentEntity> comments = commentRepository.findByCampaignId(campaignId, pageable);
        return comments.map(commentEntity -> Comment.builder()
                .id(commentEntity.getId())
                .createdAt(commentEntity.getCreatedAt())
                .content(commentEntity.getContent())
                .campaign(Campaign.builder()
                        .id(commentEntity.getId())
                        .build())
                .user(User.builder()
                        .id(commentEntity.getUser().getId())
                        .avatar(commentEntity.getUser().getAvatar())
                        .email(commentEntity.getUser().getEmail())
                        .build())
                .build());
    }

    public Comment updateComment(String id, Comment comment, UserEntity user){

        CommentEntity commentEntity = commentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        if(!commentEntity.getUser().getId().equals(user.getId()) && !user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))  ){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        commentEntity.setContent(comment.getContent());
        commentEntity.setUpdatedAt(LocalDateTime.now());
        commentEntity = commentRepository.save(commentEntity);
        Comment response =  Comment.builder()
                .id(commentEntity.getId())
                .createdAt(commentEntity.getCreatedAt())
                .content(commentEntity.getContent())
                .campaign(Campaign.builder()
                        .id(commentEntity.getCampaign().getId())
                        .build())
                .user(User.builder()
                        .id(commentEntity.getUser().getId())
                        .avatar(commentEntity.getUser().getAvatar())
                        .email(commentEntity.getUser().getEmail())
                        .build())
                .build();
        messageService.updateComment(response);
        return response;
    }
    public void deleteComment(String id, UserEntity user){

        CommentEntity commentEntity = commentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        if(!commentEntity.getUser().getId().equals(user.getId()) && !user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))  ){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        Comment response =  Comment.builder()
                .id(commentEntity.getId())
                .createdAt(commentEntity.getCreatedAt())
                .content(commentEntity.getContent())
                .campaign(Campaign.builder()
                        .id(commentEntity.getCampaign().getId())
                        .build())
                .user(User.builder()
                        .id(commentEntity.getUser().getId())
                        .avatar(commentEntity.getUser().getAvatar())
                        .email(commentEntity.getUser().getEmail())
                        .build())
                .build();
        commentRepository.delete(commentEntity);

        messageService.deleteComment(response);
    }


}
