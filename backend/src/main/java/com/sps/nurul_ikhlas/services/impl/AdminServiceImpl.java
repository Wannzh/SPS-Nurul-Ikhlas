package com.sps.nurul_ikhlas.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sps.nurul_ikhlas.models.entities.AcademicYear;
import com.sps.nurul_ikhlas.models.entities.Parent;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.models.enums.AcademicYearStatus;
import com.sps.nurul_ikhlas.models.enums.Role;
import com.sps.nurul_ikhlas.models.enums.StudentStatus;
import com.sps.nurul_ikhlas.payload.request.AcademicYearRequest;
import com.sps.nurul_ikhlas.repositories.AcademicYearRepository;
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
    private final AcademicYearRepository academicYearRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // =========================================
    // STUDENT MANAGEMENT
    // =========================================

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

        String username = primaryParent.getEmail();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Akun dengan email ini sudah ada. Silakan hubungi admin.");
        }

        // Generate verification token instead of password
        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode("PENDING_SETUP_" + UUID.randomUUID().toString()))
                .role(Role.ORTU)
                .person(student.getPerson())
                .verificationToken(verificationToken)
                .isPasswordSet(false)
                .build();
        userRepository.save(user);
        log.info("Created User account for parent: {} with activation token", username);

        String studentName = student.getPerson().getFullName();
        emailService.sendActivationLink(
                primaryParent.getEmail(),
                studentName,
                username,
                verificationToken);

        return String.format("Siswa %s berhasil diverifikasi. Link aktivasi dikirim ke email %s",
                studentName, primaryParent.getEmail());
    }

    @Override
    public List<Student> getRegisteredStudents() {
        return studentRepository.findAll().stream()
                .filter(s -> s.getStatus() == StudentStatus.REGISTERED)
                .toList();
    }

    // =========================================
    // ACADEMIC YEAR CRUD
    // =========================================

    @Override
    @Transactional
    public AcademicYear createAcademicYear(AcademicYearRequest request) {
        // Validate: if status is OPEN, fee must be provided
        if (request.getStatus() == AcademicYearStatus.OPEN) {
            if (request.getRegistrationFee() == null || request.getRegistrationFee() <= 0) {
                throw new RuntimeException("Biaya pendaftaran wajib diisi untuk tahun ajaran yang OPEN");
            }
            // Close any existing OPEN academic year
            academicYearRepository.findByStatus(AcademicYearStatus.OPEN)
                    .ifPresent(existing -> {
                        existing.setStatus(AcademicYearStatus.CLOSED);
                        academicYearRepository.save(existing);
                        log.info("Closed previous academic year: {}", existing.getName());
                    });
        }

        AcademicYear academicYear = AcademicYear.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .status(request.getStatus())
                .registrationFee(request.getRegistrationFee())
                .build();

        academicYearRepository.save(academicYear);
        log.info("Created academic year: {} with fee: {}", academicYear.getName(), academicYear.getRegistrationFee());

        return academicYear;
    }

    @Override
    @Transactional
    public AcademicYear updateAcademicYear(String id, AcademicYearRequest request) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tahun ajaran tidak ditemukan"));

        // Validate: if status is being changed to OPEN, fee must be provided
        if (request.getStatus() == AcademicYearStatus.OPEN) {
            if (request.getRegistrationFee() == null || request.getRegistrationFee() <= 0) {
                throw new RuntimeException("Biaya pendaftaran wajib diisi untuk tahun ajaran yang OPEN");
            }
            // Close any other OPEN academic year (except this one)
            academicYearRepository.findByStatus(AcademicYearStatus.OPEN)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        existing.setStatus(AcademicYearStatus.CLOSED);
                        academicYearRepository.save(existing);
                        log.info("Closed previous academic year: {}", existing.getName());
                    });
        }

        academicYear.setName(request.getName());
        academicYear.setStatus(request.getStatus());
        academicYear.setRegistrationFee(request.getRegistrationFee());

        academicYearRepository.save(academicYear);
        log.info("Updated academic year: {} with fee: {}", academicYear.getName(), academicYear.getRegistrationFee());

        return academicYear;
    }

    @Override
    public List<AcademicYear> getAllAcademicYears() {
        return academicYearRepository.findAll();
    }

    @Override
    public AcademicYear getActiveAcademicYear() {
        return academicYearRepository.findByStatus(AcademicYearStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("Tidak ada tahun ajaran yang aktif"));
    }

    @Override
    @Transactional
    public void deleteAcademicYear(String id) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tahun ajaran tidak ditemukan"));

        // Prevent deleting OPEN academic year
        if (academicYear.getStatus() == AcademicYearStatus.OPEN) {
            throw new RuntimeException("Tidak dapat menghapus tahun ajaran yang sedang aktif");
        }

        academicYearRepository.delete(academicYear);
        log.info("Deleted academic year: {}", academicYear.getName());
    }
}
