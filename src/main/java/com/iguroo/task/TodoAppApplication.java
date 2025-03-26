package com.iguroo.task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import com.iguroo.task.entity.Role;
import com.iguroo.task.entity.User;
import com.iguroo.task.repo.RoleRepository;
import com.iguroo.task.repo.UserRepository;

import java.util.Set;

@SpringBootApplication
public class TodoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoAppApplication.class, args);
    }

    @Bean
    @Transactional // Ensures database consistency
    public CommandLineRunner loadRolesAndAdmin(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            // ✅ Create roles if they don't exist
            Role adminRole = roleRepository.findByRolename("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setRolename("ADMIN");
                return roleRepository.save(role);
            });

            Role userRole = roleRepository.findByRolename("USER").orElseGet(() -> {
                Role role = new Role();
                role.setRolename("USER");
                return roleRepository.save(role);
            });

            // ✅ Prevent duplicate admin creation by checking email
            if (!userRepository.existsByEmail("anusha@email.com")) { 
                User admin = new User();
                admin.setFullname("Anusha");
                admin.setUsername("anusha12");
                admin.setPassword("$2a$12$zeGyl9AVUrN8zXaUFrYoS.0ovbSstqGri0I01AT3Lkl.IO.oGlP4i"); // ⚠ No encoding as per your request
                admin.setEmail("anusha@email.com");

                // Assign ADMIN role
                admin.setRoles(Set.of(adminRole));

                userRepository.save(admin);
                System.out.println("✅ First ADMIN created successfully!");
            } else {
                System.out.println("⚠ Admin with email 'anusha@email.com' already exists. Skipping creation.");
            }
        };
    }
}