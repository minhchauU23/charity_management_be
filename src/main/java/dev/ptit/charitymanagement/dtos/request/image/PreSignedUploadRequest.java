package dev.ptit.charitymanagement.dtos.request.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreSignedUploadRequest {
    @NotNull( message = "IMAGE_NAME_INVALID")
    @NotBlank( message = "IMAGE_NAME_INVALID")
    @Pattern(regexp = ".*\\.(jpg|jpeg|png|gif)$", message = "IMAGE_NAME_INVALID")
    String fileName;
}