package com.iguroo.task.service.impl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.iguroo.task.dto.LoginDto;
import com.iguroo.task.dto.UserDto;
import com.iguroo.task.entity.Role;
import com.iguroo.task.entity.User;
import com.iguroo.task.exception.TodoApiException;
import com.iguroo.task.mapper.UserMapper;
import com.iguroo.task.repo.RoleRepository;
import com.iguroo.task.repo.UserRepository;
import com.iguroo.task.service.AuthService;

import jakarta.validation.constraints.NotNull;

@Service
public class AuthImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public String register(@NotNull UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new TodoApiException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new TodoApiException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Assign USER role by default
        Role userRole = roleRepository.findByRolename("USER")
            .orElseThrow(() -> new RuntimeException("USER role not found!"));

        // Map user data and assign default USER role
        User user = UserMapper.mapToEntity(userDto, null);
        user.setRoles(Set.of(userRole)); // Assign USER role

        userRepository.save(user);
        return "✅ User registered successfully!";
    }

    @Override
    public String login(@NotNull LoginDto loginDto) {
        Optional<User> userOpt = userRepository.findByUsername(loginDto.getUsername());

        if (userOpt.isEmpty()) {
            throw new TodoApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password!");
        }

        User user = userOpt.get();

        // Check if the passwords match (without encoding)
        if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new TodoApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password!");
        }

        return "✅ Login successful!";
    }

    @Override
    public String registerAdmin(UserDto userDto, Long adminId) {
        // Verify that the requesting user (adminId) is an ADMIN
        User adminUser = userRepository.findById(adminId)
            .orElseThrow(() -> new TodoApiException(HttpStatus.FORBIDDEN, "Admin not found!"));

        boolean isAdmin = adminUser.getRoles().stream()
            .anyMatch(role -> role.getRolename().equals("ADMIN"));

        if (!isAdmin) {
            throw new TodoApiException(HttpStatus.FORBIDDEN, "Only an ADMIN can create another ADMIN!");
        }

        // Check if the new admin's username and email already exist
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new TodoApiException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new TodoApiException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Assign ADMIN role
        Role adminRole = roleRepository.findByRolename("ADMIN")
            .orElseThrow(() -> new RuntimeException("ADMIN role not found!"));

        // Map user data and assign ADMIN role
        User newAdmin = UserMapper.mapToEntity(userDto, null);
        newAdmin.setRoles(Set.of(adminRole)); // Assign ADMIN role

        userRepository.save(newAdmin);
        return "✅ Admin registered successfully!";
    }
}
