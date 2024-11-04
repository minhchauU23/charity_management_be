package dev.ptit.charitymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.interning.qual.EqualsMethod;

@Entity
@Table(name = "tbl_user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode
public class UserRole {
    @EmbeddedId
    UserRoleCompositeKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(referencedColumnName = "id")
    User user;
    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(referencedColumnName = "id")
    Role role;
}
