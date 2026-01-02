package com.sps.nurul_ikhlas.services.impl;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sps.nurul_ikhlas.models.entities.Parent;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.models.enums.Role;
import com.sps.nurul_ikhlas.models.enums.StudentStatus;
import com.sps.nurul_ikhlas.repositories.ParentRepository;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.UserRepository;
import com.sps.nurul_ikhlas.services.AdminService;
import com.sps.nurul_ikhlas.services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public String verifyStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Siswa tidak ditemukan"));

        if (student.getStatus() != StudentStatus.REGISTERED) {
            throw new RuntimeException("Siswa sudah diverifikasi atau memiliki status: " + student.getStatus());
        }

        student.setStatus(StudentStatus.ACCEPTED);
        studentRepository.save(student);
        log.info("Student {} status updated to ACCEPTED", studentId);

        List<Parent> parents = parentRepository.findByStudentId(studentId);
        Parent primaryParent = parents.stream()
                .filter(p -> p.getHandphone() != null && p.getEmail() != null)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Data orang tua dengan nomor telepon dan email tidak ditemukan"));

        String username = primaryParent.getHandphone();
        String rawPassword = generateRandomPassword(6);

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Akun dengan nomor telepon ini sudah ada. Silakan hubungi admin.");
        }

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ORTU)
                .person(student.getPerson())
                .build();
        userRepository.save(user);
        log.info("Created User account for parent: {}", username);

        String studentName = student.getPerson().getFullName();
        emailService.sendAccountCredentials(
                primaryParent.getEmail(),
                studentName,
                username,
                rawPassword);

        return String.format("Siswa %s berhasil diverifikasi. Kredensial dikirim ke email %s",
                studentName, primaryParent.getEmail());
    }

    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    @Override
    public List<Student> getRegisteredStudents() {
        return studentRepository.findAll().stream()
                .filter(s -> s.getStatus() == StudentStatus.REGISTERED)
                .toList();
    }
}
