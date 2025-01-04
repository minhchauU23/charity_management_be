package dev.ptit.charitymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode
public class UserRoleEntity {
    @EmbeddedId
    UserRoleCompositeKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(referencedColumnName = "id")
    UserEntity user;
    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(referencedColumnName = "id")
    RoleEntity role;
}
