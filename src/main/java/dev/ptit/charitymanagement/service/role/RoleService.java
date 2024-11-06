package dev.ptit.charitymanagement.service.role;

import dev.ptit.charitymanagement.dtos.request.role.RoleCreateRequest;
import dev.ptit.charitymanagement.dtos.request.role.RoleUpdateRequest;
import dev.ptit.charitymanagement.dtos.response.role.RoleDTO;
import dev.ptit.charitymanagement.dtos.response.user.UserDTO;
import dev.ptit.charitymanagement.entity.Role;
import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import dev.ptit.charitymanagement.repository.RoleRepository;
import dev.ptit.charitymanagement.repository.UserRepository;
import dev.ptit.charitymanagement.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleService {
    RoleRepository roleRepository;
    UserRepository userRepository;

    public RoleDTO findById(Long roleId){
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public List<RoleDTO> findAll(){
        return roleRepository.findAll().stream().map(role -> RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build()).toList();
    }

    public RoleDTO create(RoleCreateRequest request){
        Role role =  roleRepository.save(Role.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                .build());
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public RoleDTO update(Long id, RoleUpdateRequest request){
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role = roleRepository.save(role);
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public void delete(Long id){
         roleRepository.deleteById(id);
    }

    public Page<UserDTO> getUserOfRoles( Long id, Integer page, Integer pageSize, String searchQuery, String sortRaw){
        String[] sortToArr = sortRaw.split(",");
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortToArr[1].equals("asc")? Sort.Direction.ASC: Sort.Direction.DESC, sortToArr[0]));
        return userRepository.findByRoleId(id, searchQuery, pageable).map(user -> UserDTO.builder()
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
