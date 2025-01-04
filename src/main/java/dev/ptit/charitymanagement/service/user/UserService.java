package dev.ptit.charitymanagement.service.user;

import dev.ptit.charitymanagement.dtos.ChangePasswordRequest;
import dev.ptit.charitymanagement.dtos.Image;
import dev.ptit.charitymanagement.dtos.Role;
import dev.ptit.charitymanagement.dtos.User;
import dev.ptit.charitymanagement.entity.RoleEntity;
import dev.ptit.charitymanagement.entity.UserEntity;
import dev.ptit.charitymanagement.entity.UserRoleEntity;
import dev.ptit.charitymanagement.entity.UserRoleCompositeKey;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.RoleRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.repository.UserRoleRepository;
import dev.ptit.charitymanagement.service.image.S3CloudService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserRoleRepository userRoleRepository;
    CacheManager cacheManager;
    S3CloudService s3CloudService;
    public Page<User> findAll(Integer page, Integer pageSize, String searchKeyWord, String sortRaw){
        String[] sortToArr = sortRaw.split(",");
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortToArr[1].equals("asc")? Sort.Direction.ASC: Sort.Direction.DESC, sortToArr[0]));
        Page<UserEntity> userActive;

        if(searchKeyWord.trim().isEmpty()){
            userActive = userRepository.findAllUser( pageable);
           log.info("find all user");
        }
        else {
            userActive = userRepository.search( searchKeyWord.trim(), pageable);
            log.info("search all user");
        }
        return userActive.map(user -> User.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .dob(user.getDob())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .locked(user.isLocked())
                .build());
    }

    public User findById(Long id) {
        UserEntity userEntity = userRepository.findByIdWithRoles(id).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        return User.builder()
                .id(userEntity.getId())
                .avatar(userEntity.getAvatar())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .dob(userEntity.getDob())
                .phone(userEntity.getPhone())
                .address(userEntity.getAddress())
                .locked(userEntity.isLocked())
                .roles(userEntity.getUserRoles().stream().map(userRole -> Role.builder()
                        .id(userRole.getRole().getId())
                        .name(userRole.getRole().getName())
                        .build()).toList())
                .build();
    }

    public User create(User user) {
        if(userRepository.existsByEmail(user.getEmail())) throw  new AppException(ErrorCode.USER_EXISTED);
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .phone(user.getPhone())
                .address(user.getAddress())
                .locked(false)
                .build();

        Set<UserRoleEntity> userRoles = new HashSet<>();
        for(Role role : user.getRoles()){

            userRoles.add(UserRoleEntity.builder()
                    .id(new UserRoleCompositeKey(user.getId(), role.getId()))
                    .user(userEntity)
                    .role(roleRepository.findById(role.getId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)))
                    .build());
        }
        userEntity.setUserRoles(userRoles);
        userEntity =  userRepository.save(userEntity);
        return User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .dob(userEntity.getDob())
                .phone(userEntity.getPhone())
                .address(userEntity.getAddress())
                .roles(userEntity.getUserRoles().stream().map(userRole -> Role.builder()
                        .id(userRole.getRole().getId())
                        .name(userRole.getRole().getName())
                        .build()).toList())
                .build();
    }

    public Image updateAvatar(Long id, Image image){


        UserEntity user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String folder = "user-" + user.getId();
        String key = folder+"/" + image.getFileName();
        String preSignedUrl = s3CloudService.generatePreSignedUrl(key);
        String fileLink = s3CloudService.generateFileLink(key);
        user.setAvatar(fileLink);
        user = userRepository.save(user);
        image.setUrl(fileLink);
        image.setFolder(folder);
        image.setPreSignedUrl(preSignedUrl);

        return image;
    }

    @Transactional
    public User update(Long id, User user) {
        UserEntity userEntity = userRepository.findByIdWithRoles(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setAddress(user.getAddress());
        userEntity.setGender(user.getGender());
        userEntity.setPhone(user.getPhone());
        userEntity.setLocked(user.isLocked());
        userEntity.setDob(user.getDob());

        Set<UserRoleEntity> newRoles = new HashSet<>();
        for (Role role: user.getRoles()){
            UserRoleEntity userRole = UserRoleEntity.builder()
                    .id(new UserRoleCompositeKey(user.getId(), role.getId()))
                    .role(roleRepository.findById(role.getId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)))
                    .user(userEntity)
                    .build();
            log.info("infor role: {} {}", role.getId(), role.getId().getClass());
            log.info("Contain : {}", newRoles.contains(userRole));
            newRoles.add(userRole);
        }


        log.info("Side of new Roles {}", newRoles.size());

        // Cập nhật bộ UserRole của user
        Set<UserRoleEntity> currentRoles = userEntity.getUserRoles();
        currentRoles.removeIf(role -> !newRoles.contains(role));
        currentRoles.addAll(newRoles);


        userEntity = userRepository.save(userEntity);


        return User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .avatar(userEntity.getAvatar())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .dob(userEntity.getDob())
                .address(userEntity.getAddress())
                .phone(userEntity.getPhone())
                .locked(userEntity.isLocked())
                .roles(userEntity.getUserRoles().stream().map(usRole -> Role.builder()
                        .id(usRole.getRole().getId())
                        .name(usRole.getRole().getName())
                        .build()).toList())
                .build();
    }

    public void register(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        UserEntity userEntity = UserEntity.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .locked(false)
                .build();

        RoleEntity role = roleRepository.findById(2L).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setId(new UserRoleCompositeKey(user.getId(), role.getId()));
        userRole.setUser(userEntity);
        userRole.setRole(role);
        userEntity.setUserRoles(Set.of(userRole));
        userRepository.save(userEntity);
    }

    public User getProfile(Long id){
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
        return User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .avatar(userEntity.getAvatar())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .dob(userEntity.getDob())
                .address(userEntity.getAddress())
                .phone(userEntity.getPhone())
                .build();
    }

    public User updateProfile(Long id, User user){
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
        userEntity.setAddress(user.getAddress());
        userEntity.setGender(user.getGender());
        userEntity.setPhone(user.getPhone());
        userEntity = userRepository.save(userEntity);
        return User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .avatar(userEntity.getAvatar())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .dob(userEntity.getDob())
                .address(userEntity.getAddress())
                .phone(userEntity.getPhone())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmailWithRoles(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public void changePassword(Long id, ChangePasswordRequest request){
        UserEntity user = userRepository.findById(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
        log.info("oldPassword: {}, newPassword: {}, repeat new Password {}", request.getOldPassword(), request.getNewPassword(), request.getRepeatNewPassword());
        log.info("saved password {}", user.getPassword() );
        log.info("Equals: {}", passwordEncoder.matches(request.getOldPassword(), user.getPassword()));
        if(!request.getNewPassword().trim().equals(request.getRepeatNewPassword().trim())){
            throw new AppException(ErrorCode.REPEAT_PASSWORD_NOT_MATCHING);
        }
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            throw new AppException(ErrorCode.BAD_CREDENTIALS);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        cacheManager.getCache("jwt_blacklist").put(user.getEmail(), new Date());
//        log.info("change password at {}", cacheManager.getCache("jwt_blacklist").get(user.getEmail()));
        Date date = (Date) cacheManager.getCache("jwt_blacklist").get(user.getEmail()).get();
        log.info("change at {}", date);
    }

}
