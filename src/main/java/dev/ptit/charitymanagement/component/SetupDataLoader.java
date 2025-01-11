package dev.ptit.charitymanagement.component;

import dev.ptit.charitymanagement.entity.RoleEntity;
import dev.ptit.charitymanagement.entity.UserEntity;
import dev.ptit.charitymanagement.entity.UserRoleEntity;
import dev.ptit.charitymanagement.entity.UserRoleCompositeKey;
import dev.ptit.charitymanagement.repository.RoleRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
//
@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    boolean alreadySetup = false;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(alreadySetup) return;
        RoleEntity roleAdmin = createRoleIfNotExist("ROLE_ADMIN");
        RoleEntity roleUser = createRoleIfNotExist("ROLE_USER");

        UserEntity admin = UserEntity.builder()
                .email("givewellvn@gmail.com")
                .password(passwordEncoder.encode("123456789"))
                .avatar("https://givewellvnbucket.s3.ap-southeast-2.amazonaws.com/avatar-default/user-member-avatar-people-heart.png")
                .phone("0396024810")
                .address("Cụm 1, Nhân Hiền, Hiền Giang, Thường Tín, Hà Nội")
                .firstName("givewell")
                .dob(LocalDate.now())
                .gender("Nam")
                .lastName("vn")
                .locked(false)
                .build();
        admin = createUserIfNotExist(admin);
        UserRoleEntity adminRole = new UserRoleEntity();
        adminRole.setRole(roleAdmin);
        adminRole.setUser(admin);
        adminRole.setId(new UserRoleCompositeKey(admin.getId(), roleAdmin.getId()));
        createUserRoleIfNotExist(adminRole);
        for(int i = 1; i <= 100; i++){
            UserEntity user = UserEntity.builder()
                    .email(String.format("tranminh%d@gmail.com", i))
                    .password(passwordEncoder.encode("123456789"))
                    .avatar("https://givewellvnbucket.s3.ap-southeast-2.amazonaws.com/avatar-default/user-member-avatar-people-heart.png")
                    .phone("0396024810")
                    .address("Cụm 1, Nhân Hiền, Hiền Giang, Thường Tín, Hà Nội")
                    .firstName("givewell")
                    .lastName("vn")
                    .dob(LocalDate.now())
                    .gender("Nam")
                    .locked(false)
                    .build();
            user = createUserIfNotExist(user);

            UserRoleEntity role = new UserRoleEntity();
            role.setRole(roleUser);
            role.setUser(user);
            role.setId(new UserRoleCompositeKey(user.getId(), roleUser.getId()));
            createUserRoleIfNotExist(role);
            alreadySetup = true;
        }



    }

    private RoleEntity createRoleIfNotExist(String name){
        Optional<RoleEntity> roleOptional = roleRepository.findByName(name);
        if(roleOptional.isPresent()) return roleOptional.get();
        RoleEntity role = new RoleEntity();
        role.setName(name);
        return roleRepository.save(role);
    }

    private UserEntity createUserIfNotExist(UserEntity user){
        Optional<UserEntity> userOptional = userRepository.findByEmail(user.getEmail());
        return userOptional.orElseGet(() -> userRepository.save(user));
    }
    private UserRoleEntity createUserRoleIfNotExist(UserRoleEntity userRole){
        Optional<UserRoleEntity> userRoleOptional = userRoleRepository.findById(userRole.getId());
        return userRoleOptional.orElseGet(() -> userRoleRepository.save(userRole));
    }
}
