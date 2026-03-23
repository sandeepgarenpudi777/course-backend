package com.course.registration.serviceImpl;

import com.course.registration.dto.RegisterRequest;
import com.course.registration.entity.Instructor;
import com.course.registration.entity.Student;
import com.course.registration.entity.User;
import com.course.registration.repository.InstructorRepository;
import com.course.registration.repository.StudentRepository;
import com.course.registration.repository.UserRepository;
import com.course.registration.service.AuthService;
import com.course.registration.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public Map<String, Object> login(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtUtil.generateToken(userDetails);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        return response;
    }

    @Override
    public Map<String, Object> register(RegisterRequest request) {
        String username = request.getUsername();
        String email    = request.getEmail();

        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("Username already exists: " + username);
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email already registered: " + email);

        User.Role userRole = request.getRole() != null ? request.getRole() : User.Role.STUDENT;

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .email(email)
                .role(userRole)
                .build();
        userRepository.save(user);

        // ── ISSUE 1 FIX: map all profile fields from request ──
        if (userRole == User.Role.STUDENT && !studentRepository.existsByEmail(email)) {

            // Use provided firstName/lastName, fall back to username if not supplied
            String firstName = isBlank(request.getFirstName()) ? username : request.getFirstName();
            String lastName  = isBlank(request.getLastName())  ? ""       : request.getLastName();

            // Use provided studentId, generate one if absent
            String studentId = isBlank(request.getStudentId())
                    ? "STU" + System.currentTimeMillis()
                    : request.getStudentId();

            Student student = Student.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .phone(request.getPhone())          // saves phone (may be null — that's fine)
                    .studentId(studentId)
                    .user(user)
                    .build();
            studentRepository.save(student);

        } else if (userRole == User.Role.INSTRUCTOR && !instructorRepository.existsByEmail(email)) {

            String firstName = isBlank(request.getFirstName()) ? username : request.getFirstName();
            String lastName  = isBlank(request.getLastName())  ? ""       : request.getLastName();

            Instructor instructor = Instructor.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .department(request.getDepartment())       // saves department
                    .qualification(request.getQualification()) // saves qualification
                    .user(user)
                    .build();
            instructorRepository.save(instructor);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        return response;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
