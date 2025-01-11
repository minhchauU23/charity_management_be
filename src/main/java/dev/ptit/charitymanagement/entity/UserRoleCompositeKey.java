package dev.ptit.charitymanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserRoleCompositeKey implements Serializable {
    @Column(name = "user_id")
    Long userId;
    @Column(name = "role_id")
    Long roleId;

}
