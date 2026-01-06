package com.sps.nurul_ikhlas.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sps.nurul_ikhlas.models.entities.AcademicYear;
import com.sps.nurul_ikhlas.models.entities.BillType;
import com.sps.nurul_ikhlas.models.entities.Parent;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.Uniform;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.models.enums.AcademicYearStatus;
import com.sps.nurul_ikhlas.models.enums.Role;
import com.sps.nurul_ikhlas.models.enums.StudentStatus;
import com.sps.nurul_ikhlas.payload.request.AcademicYearRequest;
import com.sps.nurul_ikhlas.payload.request.BillTypeRequest;
import com.sps.nurul_ikhlas.payload.request.UniformRequest;
import com.sps.nurul_ikhlas.repositories.AcademicYearRepository;
import com.sps.nurul_ikhlas.repositories.BillTypeRepository;
import com.sps.nurul_ikhlas.repositories.ParentRepository;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.UniformRepository;
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
    private final BillTypeRepository billTypeRepository;
    private final UniformRepository uniformRepository;
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
        if (request.getStatus() == AcademicYearStatus.OPEN) {
            if (request.getRegistrationFee() == null || request.getRegistrationFee() <= 0) {
                throw new RuntimeException("Biaya pendaftaran wajib diisi untuk tahun ajaran yang OPEN");
            }
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

        if (request.getStatus() == AcademicYearStatus.OPEN) {
            if (request.getRegistrationFee() == null || request.getRegistrationFee() <= 0) {
                throw new RuntimeException("Biaya pendaftaran wajib diisi untuk tahun ajaran yang OPEN");
            }
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

        if (academicYear.getStatus() == AcademicYearStatus.OPEN) {
            throw new RuntimeException("Tidak dapat menghapus tahun ajaran yang sedang aktif");
        }

        academicYearRepository.delete(academicYear);
        log.info("Deleted academic year: {}", academicYear.getName());
    }

    // =========================================
    // BILL TYPE CRUD
    // =========================================

    @Override
    @Transactional
    public BillType createBillType(BillTypeRequest request) {
        BillType billType = BillType.builder()
                .name(request.getName())
                .amount(request.getAmount())
                .period(request.getPeriod())
                .description(request.getDescription())
                .build();

        billTypeRepository.save(billType);
        log.info("Created bill type: {} with amount: {}", billType.getName(), billType.getAmount());

        return billType;
    }

    @Override
    @Transactional
    public BillType updateBillType(String id, BillTypeRequest request) {
        BillType billType = billTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jenis tagihan tidak ditemukan"));

        billType.setName(request.getName());
        billType.setAmount(request.getAmount());
        billType.setPeriod(request.getPeriod());
        billType.setDescription(request.getDescription());

        billTypeRepository.save(billType);
        log.info("Updated bill type: {} with amount: {}", billType.getName(), billType.getAmount());

        return billType;
    }

    @Override
    public List<BillType> getAllBillTypes() {
        return billTypeRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteBillType(String id) {
        BillType billType = billTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jenis tagihan tidak ditemukan"));

        billTypeRepository.delete(billType);
        log.info("Deleted bill type: {}", billType.getName());
    }

    // =========================================
    // UNIFORM CRUD
    // =========================================

    @Override
    @Transactional
    public Uniform createUniform(UniformRequest request) {
        Uniform uniform = Uniform.builder()
                .name(request.getName())
                .size(request.getSize())
                .price(request.getPrice())
                .stock(request.getStock())
                .description(request.getDescription())
                .build();

        uniformRepository.save(uniform);
        log.info("Created uniform: {} size {} with price: {}", uniform.getName(), uniform.getSize(),
                uniform.getPrice());

        return uniform;
    }

    @Override
    @Transactional
    public Uniform updateUniform(String id, UniformRequest request) {
        Uniform uniform = uniformRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seragam tidak ditemukan"));

        uniform.setName(request.getName());
        uniform.setSize(request.getSize());
        uniform.setPrice(request.getPrice());
        uniform.setStock(request.getStock());
        uniform.setDescription(request.getDescription());

        uniformRepository.save(uniform);
        log.info("Updated uniform: {} size {} with price: {}", uniform.getName(), uniform.getSize(),
                uniform.getPrice());

        return uniform;
    }

    @Override
    public List<Uniform> getAllUniforms() {
        return uniformRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUniform(String id) {
        Uniform uniform = uniformRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seragam tidak ditemukan"));

        uniformRepository.delete(uniform);
        log.info("Deleted uniform: {} size {}", uniform.getName(), uniform.getSize());
    }
}
