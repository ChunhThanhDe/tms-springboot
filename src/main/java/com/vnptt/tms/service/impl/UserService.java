package com.vnptt.tms.service.impl;

import com.vnptt.tms.api.input.LoginRequest;
import com.vnptt.tms.config.ERole;
import com.vnptt.tms.converter.UserConverter;
import com.vnptt.tms.dto.UserDTO;
import com.vnptt.tms.entity.ListDeviceEntity;
import com.vnptt.tms.entity.RolesEntity;
import com.vnptt.tms.entity.UserEntity;
import com.vnptt.tms.exception.ResourceNotFoundException;
import com.vnptt.tms.repository.ListDeviceRepository;
import com.vnptt.tms.repository.RolesRepository;
import com.vnptt.tms.repository.UserRepository;
import com.vnptt.tms.security.jwt.JwtUtils;
import com.vnptt.tms.security.responce.JwtResponse;
import com.vnptt.tms.security.services.UserDetailsImpl;
import com.vnptt.tms.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// @Service là một annotation trong Spring Framework được sử dụng để đánh dấu một lớp là một dịch vụ (service).
// Khi một lớp được đánh dấu bằng @Service, nó cho biết rằng lớp đó chịu trách nhiệm thực hiện các nhiệm vụ,
// xử lý logic và tương tác với các thành phần khác trong ứng dụng.
@Service
public class UserService implements IUserService {//, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private ListDeviceRepository listDeviceRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * update user for put request
     *
     * @param userDTO
     * @return
     */
    @Override
    public UserDTO update(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();

        Optional<UserEntity> oldUserEntity = userRepository.findById(userDTO.getId());
        userEntity = userConverter.toEntity(userDTO, oldUserEntity.get());

        try {
            userEntity = userRepository.save(userEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userConverter.toDTO(userEntity);
    }

    /**
     * update password for user
     *
     * @param id
     * @param passwordold
     * @param passwordnew
     * @return
     */
    @Override
    public UserDTO updatePassword(Long id, String passwordold, String passwordnew) {
        UserEntity userEntity = userRepository.findOneById(id);
        if (userEntity == null) {
            throw new ResourceNotFoundException("not found user with id = " + id);
        }
        if (!encoder.matches(passwordold, userEntity.getPassword())) {
            throw new RuntimeException("Wrong password: " + passwordold);
        }
        userEntity.setPassword(encoder.encode(passwordnew));
        userRepository.save(userEntity);
        return userConverter.toDTO(userEntity);
    }

    /**
     * update password with role admin
     *
     * @param id
     * @param passwordnew
     * @return
     */
    @Override
    public UserDTO forcedUpdatePassword(Long id, String passwordnew) {
        UserEntity userEntity = userRepository.findOneById(id);
        if (userEntity == null) {
            throw new ResourceNotFoundException("not found user with id = " + id);
        }
        userEntity.setPassword(encoder.encode(passwordnew));
        userRepository.save(userEntity);
        return userConverter.toDTO(userEntity);
    }

    @Override
    public List<UserDTO> findAllWithNameOrEmailOrUsernameOrCompany(Pageable pageable, Integer active, String name, String email, String username, String company) {
        boolean activeConvert = true;
        if (active == 0) {
            activeConvert = false;
        }
        List<UserEntity> entities = userRepository.findAllByActiveAndNameContainingOrEmailContainingOrUsernameContainingOrCompanyContainingOrderByModifiedDateDesc(pageable, activeConvert, name, email, username, company);
        List<UserDTO> result = new ArrayList<>();
        for (UserEntity item : entities) {
            UserDTO userDTO = userConverter.toDTO(item);
            result.add(userDTO);
        }
        return result;
    }

    @Override
    public Long totalItemWithNameOrEmailOrUsernameOrCompany(Integer active, String name, String email, String username, String company) {
        boolean activeConvert = true;
        if (active == 0) {
            activeConvert = false;
        }
        return userRepository.countAllByActiveAndNameContainingOrEmailContainingOrUsernameContainingOrCompanyContaining(activeConvert, name, email, username, company);
    }

    @Override
    public Long totalItemWithActive(Integer active) {
        boolean activeConvert = true;
        if (active == 0) {
            activeConvert = false;
        }
        return userRepository.countAllByActive(activeConvert);
    }

    /**
     * get all user manager list
     *
     * @param listDeviceId
     * @param pageable
     * @return
     */
    @Override
    public List<UserDTO> findUserManagementListDevice(Long listDeviceId, Pageable pageable) {
        List<UserDTO> result = new ArrayList<>();
        ListDeviceEntity listDevice = listDeviceRepository.findOneById(listDeviceId);
        if (listDevice == null) {
            throw new ResourceNotFoundException("not found list device with Id = " + listDeviceId);
        }
        List<UserEntity> userEntities = userRepository.findAllByDeviceEntitiesIdOrderByModifiedDateDesc(listDeviceId, pageable);
        for (UserEntity entity : userEntities) {
            result.add(userConverter.toDTO(entity));
        }
        return result;
    }

    @Override
    public UserDTO forceUpdate(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();

        Optional<UserEntity> oldUserEntity = userRepository.findById(userDTO.getId());
        userEntity = userConverter.toEntity(userDTO, oldUserEntity.get());

        List<RolesEntity> ruleEntities = new ArrayList<>();
        List<String> rules = userDTO.getRulename();
        if (rules != null) {
            if (rules.size() > 3 || rules.size() == 0) {
                throw new ResourceNotFoundException("number of rule name is wrong ");
            }
            for (String iteam : rules) {
                RolesEntity rolesEntity = rolesRepository.findOneByName(ERole.valueOf(iteam));
                if (rolesEntity == null) {
                    throw new ResourceNotFoundException("can't not found rule with rule_name = " + userDTO.getRulename());
                }
                ruleEntities.add(rolesEntity);
            }
            userEntity.removeRoleDevice();

            for (RolesEntity entity : ruleEntities) {
                userEntity.addRole(entity);
            }
        }

        try {
            userEntity = userRepository.save(userEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userConverter.toDTO(userEntity);
    }

    @Override
    public List<UserDTO> findUserManagementListDeviceWithName(Long listDeviceId, String search, Pageable pageable) {
        List<UserDTO> result = new ArrayList<>();
        ListDeviceEntity listDevice = listDeviceRepository.findOneById(listDeviceId);
        if (listDevice == null) {
            throw new ResourceNotFoundException("not found list device with Id = " + listDeviceId);
        }
        List<UserEntity> userEntities = userRepository.findAllByDeviceEntitiesIdAndNameContainingOrderByModifiedDateDesc(listDeviceId, search, pageable);
        for (UserEntity entity : userEntities) {
            result.add(userConverter.toDTO(entity));
        }
        return result;
    }

    @Override
    public Long totalUserManagementListDeviceWithName(Long listDeviceId, String search) {
        return userRepository.countByDeviceEntitiesIdAndNameContaining(listDeviceId, search);
    }

    @Override
    public Long totalUserManagementListDevice(Long listDeviceId) {
        return userRepository.countByDeviceEntitiesId(listDeviceId);
    }

    @Override
    public void remove(Long id) {
        UserEntity userEntity = userRepository.findOneById(id);
        if (userEntity == null) {
            throw new ResourceNotFoundException("not found user with id = " + id);
        }
        userEntity.setActive(false);
        userRepository.save(userEntity);
    }

    @Override
    public List<UserDTO> findAllWithActive(Pageable pageable, Integer active) {
        boolean activeConvert = true;
        if (active == 0) {
            activeConvert = false;
        }
        List<UserEntity> entities = userRepository.findAllByActiveOrderByModifiedDateDesc(pageable, activeConvert);
        List<UserDTO> result = new ArrayList<>();
        for (UserEntity item : entities) {
            UserDTO userDTO = userConverter.toDTO(item);
            result.add(userDTO);
        }
        return result;
    }

    @Override
    public List<UserDTO> findAllWithActive(Integer active) {
        boolean activeConvert = true;
        if (active == 0) {
            activeConvert = false;
        }
        List<UserEntity> entities = userRepository.findAllByActiveOrderByModifiedDateDesc(activeConvert);
        List<UserDTO> result = new ArrayList<>();
        for (UserEntity item : entities) {
            UserDTO userDTO = userConverter.toDTO(item);
            result.add(userDTO);
        }
        return result;
    }


    @Override
    public UserDTO findOne(Long id) {
        UserEntity entity = userRepository.findOneById(id);
        if (entity == null) {
            throw new ResourceNotFoundException("not found user with id = " + id);
        }
        return userConverter.toDTO(entity);
    }

    /**
     * find item with page number and totalPage number
     *
     * @param pageable
     * @return
     */
    @Override
    public List<UserDTO> findAll(Pageable pageable) {
        List<UserEntity> entities = userRepository.findAll(pageable).getContent();
        List<UserDTO> result = new ArrayList<>();
        for (UserEntity item : entities) {
            UserDTO userDTO = userConverter.toDTO(item);
            result.add(userDTO);
        }
        return result;
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserEntity> entities = userRepository.findAll();
        List<UserDTO> result = new ArrayList<>();
        for (UserEntity item : entities) {
            UserDTO userDTO = userConverter.toDTO(item);
            result.add(userDTO);
        }
        return result;
    }

    /**
     * todo modify
     *
     * @param ruleIds
     * @return
     */
    @Override
    public List<UserDTO> findAllWithRule(Long[] ruleIds) {
//        List<RuleEntity> ruleEntities = new ArrayList<>();
//        for (Long id : ruleIds) {
//            RuleEntity ruleEntity = ruleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found rule with id = " + id));
//            ruleEntities.add(ruleEntity);
//        }
//        List<UserEntity> userEntities = userRepository.findAllByRuleEntities(ruleEntities);
//        List<UserDTO> result = new ArrayList<>();
//        for (UserEntity entity : userEntities) {
//            UserDTO userDTO = userConverter.toDTO(entity);
//            result.add(userDTO);
//        }
//        return result;
        return null;
    }

    /**
     * create new account
     *
     * @param model model user DTO
     * @return user DTO after save on database
     */
    @Override
    public UserDTO signup(UserDTO model) {
        if (userRepository.existsByUsername(model.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        UserEntity userEntity = userConverter.toEntity(model);
        userEntity.setActive(true);

        // Create new user's account
        userEntity.setPassword(encoder.encode(model.getPassword()));

        List<String> strRoles = model.getRulename();
        List<RolesEntity> roles = new ArrayList<>();

        if (strRoles == null) {
            RolesEntity userRole = rolesRepository.findByName(ERole.ROLE_USER);
            if (userRole == null) {
                throw new RuntimeException("Error: Role is not found.");
            }
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        RolesEntity adminRole = rolesRepository.findByName(ERole.ROLE_ADMIN);
                        if (adminRole == null) {
                            throw new RuntimeException("Error: Role is not found.");
                        }
                        roles.add(adminRole);
                        break;
                    case "mod":
                        RolesEntity modRole = rolesRepository.findByName(ERole.ROLE_MODERATOR);
                        if (modRole == null) {
                            throw new RuntimeException("Error: Role is not found.");
                        }
                        roles.add(modRole);

                        break;
                    default:
                        RolesEntity userRole = rolesRepository.findByName(ERole.ROLE_USER);
                        if (userRole == null) {
                            throw new RuntimeException("Error: Role is not found.");
                        }
                        roles.add(userRole);
                }
            });
        }


        userEntity.setRuleEntities(roles);
        userRepository.save(userEntity);

        return userConverter.toDTO(userEntity);
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> rules = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        rules,
                        "TMS"));
    }

    @Override
    public int totalItem() {
        return (int) userRepository.count();
    }

    @Override
    public void delete(Long[] ids) {
        for (Long item : ids) {
            userRepository.deleteById(item);
        }
    }
}
