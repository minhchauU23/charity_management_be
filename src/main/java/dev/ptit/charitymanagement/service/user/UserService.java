package dev.ptit.charitymanagement.service.user;

import dev.ptit.charitymanagement.dtos.request.image.PreSignedUploadRequest;
import dev.ptit.charitymanagement.dtos.request.user.*;
import dev.ptit.charitymanagement.dtos.response.image.PresignedUploadDTO;
import dev.ptit.charitymanagement.dtos.response.role.RoleDTO;
import dev.ptit.charitymanagement.dtos.response.user.UserDTO;
import dev.ptit.charitymanagement.entity.Role;
import dev.ptit.charitymanagement.entity.User;
import dev.ptit.charitymanagement.entity.UserRole;
import dev.ptit.charitymanagement.entity.UserRoleCompositeKey;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.RoleRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.repository.UserRoleRepository;
import dev.ptit.charitymanagement.service.image.ImageService;
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

import java.time.LocalDate;
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
    ImageService imageService;
    public Page<UserDTO> findAll(Integer page, Integer pageSize, String searchKeyWord, String sortRaw){
        String[] sortToArr = sortRaw.split(",");
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortToArr[1].equals("asc")? Sort.Direction.ASC: Sort.Direction.DESC, sortToArr[0]));
        Page<User> userActive;

        if(searchKeyWord.trim().isEmpty()){
            userActive = userRepository.findAllUser( pageable);
           log.info("find all user");
        }
        else {
            userActive = userRepository.search( searchKeyWord.trim(), pageable);
            log.info("search all user");
        }
        return userActive.map(user -> UserDTO.builder()
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

    public UserDTO findById(Long id) {
        User user = userRepository.findByIdWithRoles(id).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserDTO.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .phone(user.getPhone())
                .address(user.getAddress())
                .locked(user.isLocked())
                .roles(user.getUserRoles().stream().map(userRole -> RoleDTO.builder()
                        .id(userRole.getRole().getId())
                        .name(userRole.getRole().getName())
                        .build()).toList())
                .build();
    }

    public UserDTO create(UserCreateRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) throw  new AppException(ErrorCode.USER_EXISTED);
        User user = User.builder()
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .dob(userRequest.getDob())
                .phone(userRequest.getPhone())
                .address(userRequest.getAddress())
                .locked(false)
                .build();

        Set<UserRole> userRoles = new HashSet<>();
        for(RoleDTO role : userRequest.getRoles()){

            userRoles.add(UserRole.builder()
                    .id(new UserRoleCompositeKey(user.getId(), role.getId()))
                    .user(user)
                    .role(roleRepository.findById(role.getId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)))
                    .build());
        }
        user.setUserRoles(userRoles);
        user =  userRepository.save(user);
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .phone(user.getPhone())
                .address(user.getAddress())
                .roles(user.getUserRoles().stream().map(userRole -> RoleDTO.builder()
                        .id(userRole.getRole().getId())
                        .name(userRole.getRole().getName())
                        .build()).toList())
                .build();
    }

    public PresignedUploadDTO updateAvatar(Long id, PreSignedUploadRequest request){
        log.info(request.getFileName());
        String fileName = "avatar-"+id+imageService.extractFileExtension(request.getFileName());
        String preSignedUploadAvatar =  imageService.generatePreSignedUrl(fileName);
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setAvatar(imageService.generateFileLink(fileName));
        user = userRepository.save(user);
        return PresignedUploadDTO.builder()
                .key(fileName)

                .url(preSignedUploadAvatar)
                .linkImage(user.getAvatar())
                .build();
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateRequest userRequest) {
        User user = userRepository.findByIdWithRoles(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setAddress(userRequest.getAddress());
        user.setGender(userRequest.getGender());
        user.setPhone(userRequest.getPhone());
        user.setLocked(userRequest.isLocked());
        user.setDob(userRequest.getDob());

        Set<UserRole> newRoles = new HashSet<>();
        for (RoleDTO roleDTO: userRequest.getRoles()){
            UserRole userRole = UserRole.builder()
                    .id(new UserRoleCompositeKey(user.getId(), roleDTO.getId()))
                    .role(roleRepository.findById(roleDTO.getId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)))
                    .user(user)
                    .build();
            log.info("infor role: {} {}", roleDTO.getId(), roleDTO.getId().getClass());
            log.info("Contain : {}", newRoles.contains(userRole));
            newRoles.add(userRole);
        }


        log.info("Side of new Roles {}", newRoles.size());

        // Cập nhật bộ UserRole của user
        Set<UserRole> currentRoles = user.getUserRoles();
        currentRoles.removeIf(role -> !newRoles.contains(role));
        currentRoles.addAll(newRoles);


        user = userRepository.save(user);


        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .address(user.getAddress())
                .phone(user.getPhone())
                .locked(user.isLocked())
                .roles(user.getUserRoles().stream().map(usRole -> RoleDTO.builder()
                        .id(usRole.getRole().getId())
                        .name(usRole.getRole().getName())
                        .build()).toList())
                .build();
    }

    public void register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .locked(false)
                .build();

        Role role = roleRepository.findById(2L).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        UserRole userRole = new UserRole();
        userRole.setId(new UserRoleCompositeKey(user.getId(), role.getId()));
        userRole.setUser(user);
        userRole.setRole(role);
        user.setUserRoles(Set.of(userRole));
        userRepository.save(user);
    }

    public UserDTO getProfile(Long id){
        User user = userRepository.findById(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .address(user.getAddress())
                .phone(user.getPhone())
                .build();
    }

    public UserDTO updateProfile(Long id, UpdateProfileRequest request){
        User user = userRepository.findById(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        user.setDob(request.getDob());
        user = userRepository.save(user);
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .dob(user.getDob())
                .address(user.getAddress())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmailWithRoles(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public void changePassword(Long id, ChangePasswordRequest request){
        User user = userRepository.findById(id).orElseThrow(() ->new AppException(ErrorCode.USER_NOT_EXISTED));
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
