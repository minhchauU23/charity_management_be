package dev.ptit.charitymanagement.component;

import dev.ptit.charitymanagement.entity.Role;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.entity.UserRole;
import dev.ptit.charitymanagement.repository.RoleRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;

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
//        if(alreadySetup) return;
//        Role roleAdmin = createRoleIfNotExist("ROLE_ADMIN");
//        Role roleUser = createRoleIfNotExist("ROLE_USER");
//
//        User admin = User.builder()
//                .email("givewellvn@gmail.com")
//                .password(passwordEncoder.encode("123456789"))
//                .phone("0396024810")
//                .address("Cụm 1, Nhân Hiền, Hiền Giang, Thường Tín, Hà Nội")
//                .firstName("givewell")
//                .dob(LocalDate.now())
//                .gender("Nam")
//                .lastName("vn")
//                .build();
//        admin.setEnabled(true);
//        admin = createUserIfNotExist(admin);
//        UserRole adminRole = new UserRole();
//        adminRole.setRole(roleAdmin);
//        adminRole.setUser(admin);
//        createUserRoleIfNotExist(adminRole);
//        for(int i = 1; i <= 100; i++){
//            User user = User.builder()
//                    .email(String.format("tranminh%d@gmail.com", i))
//                    .password(passwordEncoder.encode("123456789"))
//                    .phone("0396024810")
//                    .address("Cụm 1, Nhân Hiền, Hiền Giang, Thường Tín, Hà Nội")
//                    .firstName("givewell")
//                    .lastName("vn")
//                    .dob(LocalDate.now())
//                    .gender("Nữ")
//                    .build();
//            user.setEnabled(true);
//            user = createUserIfNotExist(user);
//
//            UserRole role = new UserRole();
//            role.setRole(roleUser);
//            role.setUser(user);
//            createUserRoleIfNotExist(role);
//            alreadySetup = true;
//        }



    }

    private Role createRoleIfNotExist(String name){
        Optional<Role> roleOptional = roleRepository.findByName(name);
        if(roleOptional.isPresent()) return roleOptional.get();
        Role role = new Role();
        role.setName(name);
        role.setEnabled(true);
        return roleRepository.save(role);
    }

    private User createUserIfNotExist(User user){
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        return userOptional.orElseGet(() -> userRepository.save(user));
    }
    private UserRole createUserRoleIfNotExist(UserRole userRole){
        Optional<UserRole> userRoleOptional = userRoleRepository.findByRoleIdAndUserEmail(userRole.getRole().getId(), userRole.getUser().getEmail());
        return userRoleOptional.orElseGet(() -> userRoleRepository.save(userRole));
    }
}
