package dev.ptit.charitymanagement.service.role;

import dev.ptit.charitymanagement.dtos.Role;
import dev.ptit.charitymanagement.dtos.User;
import dev.ptit.charitymanagement.entity.RoleEntity;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.RoleRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleService {
    RoleRepository roleRepository;
    UserRepository userRepository;

    public Role findById(Long roleId){
        RoleEntity roleEntity = roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        return Role.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .descriptions(roleEntity.getDescription())
                .build();
    }

    public Page<Role> findAll(Integer page,Integer pageSize,String searchKeyWord, String sortRaw){
        String[] sortToArr = sortRaw.split(",");
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortToArr[1].equals("asc")? Sort.Direction.ASC: Sort.Direction.DESC, sortToArr[0]));
        Page<RoleEntity> roles;

        if(searchKeyWord.trim().isEmpty()){
            roles = roleRepository.findAll( pageable);
        }
        else {
            roles = roleRepository.search( searchKeyWord.trim(), pageable);
        }
        return roles.map(role -> Role.builder()
                .id(role.getId())
                .name(role.getName())
                .descriptions(role.getDescription())
                .build());
    }

    public Role create(Role role){
        RoleEntity roleEntity =  roleRepository.save(RoleEntity.builder()
                        .name(role.getName())
                        .description(role.getDescriptions())
                .build());
        return Role.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .build();
    }

    public Role update(Long id, Role role){
        RoleEntity roleEntity = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        roleEntity.setName(role.getName());
        roleEntity.setDescription(role.getDescriptions());
        roleEntity = roleRepository.save(roleEntity);
        return Role.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .build();
    }

    public void delete(Long id){
         roleRepository.deleteById(id);
    }

    public Page<User> getUserOfRoles(Long id, Integer page, Integer pageSize, String searchQuery, String sortRaw){
        String[] sortToArr = sortRaw.split(",");
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortToArr[1].equals("asc")? Sort.Direction.ASC: Sort.Direction.DESC, sortToArr[0]));
        return userRepository.findByRoleId(id, searchQuery, pageable).map(user -> User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .dob(user.getDob())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .locked(user.isLocked())
                .build());
    }


}
