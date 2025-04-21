package ma.lyes.task.modules.user.service;

import ma.lyes.task.common.exception.ResourceNotFoundException;
import ma.lyes.task.common.exception.UnauthorizedException;
import ma.lyes.task.modules.user.dto.UserDTO;
import ma.lyes.task.modules.user.dto.UserUpdateRequest;
import ma.lyes.task.modules.user.model.User;
import ma.lyes.task.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getCurrentUser() {
        User currentUser = getCurrentUserEntity();
        return mapToDTO(currentUser);
    }

    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> ResourceNotFoundException.of("User", "id", id));
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserUpdateRequest request) {
        User currentUser = getCurrentUserEntity();
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", "id", id));

        // Only allow users to update their own profile unless they are an admin
        if (!currentUser.getId().equals(id) &&
                !currentUser.getRoles().contains("ADMIN")) {
            throw new UnauthorizedException("You can only update your own profile");
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Only admins can change roles
        if (request.getRoles() != null && !request.getRoles().isEmpty() &&
                currentUser.getRoles().contains("ADMIN")) {
            user.setRoles(request.getRoles());
        }

        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}