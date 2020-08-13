package com.example.study.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    public UserPrincipal findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        log.warn("User:{}, username: {}", user, username);
        if (null != user) {
            UserPrincipal userPrincipal = new UserPrincipal();
            Set<String> authorities = new HashSet<>();
            String roleName = user.getRoleName();
            if (null != user.getRoleName()) {
                authorities.add(roleName);
                List<RolePermission> rolePermissionList = rolePermissionRepository.findByRoleName(roleName);
                rolePermissionList.forEach(r -> {
                    authorities.add(r.getPermissionName());
                });
            }
            userPrincipal.setUserId(user.getUserId());
            userPrincipal.setUserName(user.getUsername());
            userPrincipal.setUserPassword(user.getUserPassword());
            userPrincipal.setAuthorities(authorities.stream().collect(Collectors.toList()));
            log.warn("UserPrincipal:{}", userPrincipal);
            return userPrincipal;
        }
        return null;
    }
}
