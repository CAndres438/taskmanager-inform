package services;

import com.caop.taskmanager_inform.dto.AuthRequest;
import com.caop.taskmanager_inform.dto.AuthResponse;
import com.caop.taskmanager_inform.models.Role;
import com.caop.taskmanager_inform.models.User;
import com.caop.taskmanager_inform.repositories.RoleRepository;
import com.caop.taskmanager_inform.repositories.UserRepository;
import com.caop.taskmanager_inform.security.JwtUtil;
import com.caop.taskmanager_inform.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_shouldThrowException_whenEmailExists() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");
        request.setName("Test User");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request);
        });

        assertEquals("auth.email_already_registered", exception.getMessage());
    }

    @Test
    void registerUser_shouldSaveUser_whenEmailIsNew() {
        AuthRequest request = new AuthRequest();
        request.setEmail("new@mail.com");
        request.setPassword("123456");
        request.setName("New User");

        Role mockRole = new Role(1, "ROLE_USER");

        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1);
            return u;
        });
        when(jwtUtil.generateToken("new@mail.com")).thenReturn("mocked-token");

        AuthResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals("New User", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("ROLE_USER", result.getRole());
        assertEquals("mocked-token", result.getToken());
    }
}
