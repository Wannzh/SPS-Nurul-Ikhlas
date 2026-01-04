package com.sps.nurul_ikhlas.services.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sps.nurul_ikhlas.models.entities.AcademicYear;
import com.sps.nurul_ikhlas.models.entities.Parent;
import com.sps.nurul_ikhlas.models.entities.People;
import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.Village;
import com.sps.nurul_ikhlas.models.enums.AcademicYearStatus;
import com.sps.nurul_ikhlas.models.enums.AgeGroup;
import com.sps.nurul_ikhlas.models.enums.Relation;
import com.sps.nurul_ikhlas.models.enums.StudentStatus;
import com.sps.nurul_ikhlas.payload.request.RegisterRequest;
import com.sps.nurul_ikhlas.payload.response.RegisterResponse;
import com.sps.nurul_ikhlas.repositories.AcademicYearRepository;
import com.sps.nurul_ikhlas.repositories.ParentRepository;
import com.sps.nurul_ikhlas.repositories.PeopleRepository;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.VillageRepository;
import com.sps.nurul_ikhlas.services.AuthService;
import com.sps.nurul_ikhlas.services.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final PeopleRepository peopleRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final VillageRepository villageRepository;
    private final AcademicYearRepository academicYearRepository;
    private final PaymentService paymentService;

    private static final Double DEFAULT_REGISTRATION_FEE = 150000.0; // Fallback if not set

    @Override
    @Transactional
    public RegisterResponse registerStudent(RegisterRequest request) {
        // Validate agreement
        if (!Boolean.TRUE.equals(request.getIsAgreed())) {
            throw new RuntimeException("Anda harus menyetujui persyaratan pendaftaran");
        }

        // Validate phone number uniqueness
        if (parentRepository.existsByHandphone(request.getPhoneNumber())) {
            throw new RuntimeException("Nomor telepon sudah terdaftar. Silakan gunakan nomor lain atau hubungi admin.");
        }

        // Fetch Village
        Village village = villageRepository.findById(request.getVillageId())
                .orElseThrow(() -> new RuntimeException("Desa/Kelurahan tidak ditemukan"));

        // Step 1: Fetch active Academic Year
        AcademicYear academicYear = academicYearRepository.findByStatus(AcademicYearStatus.OPEN)
                .orElseThrow(
                        () -> new RuntimeException("Pendaftaran belum dibuka. Tidak ada tahun ajaran yang aktif."));

        // Step 2: Get dynamic registration fee
        Double registrationFee = academicYear.getRegistrationFee();
        if (registrationFee == null || registrationFee <= 0) {
            log.warn("Registration fee not set for academic year: {}. Using default fee.", academicYear.getName());
            registrationFee = DEFAULT_REGISTRATION_FEE;
        }
        log.info("Registration fee for {}: Rp {}", academicYear.getName(), registrationFee);

        // Create People (Child)
        People childPerson = People.builder()
                .id(UUID.randomUUID().toString())
                .fullName(request.getChildFullName())
                .birthPlace(request.getBirthPlace())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .religion(request.getReligion())
                .village(village)
                .address(request.getAddress())
                .build();
        peopleRepository.save(childPerson);
        log.info("Created People entry for child: {}", childPerson.getFullName());

        // Calculate AgeGroup
        AgeGroup ageGroup = calculateAgeGroup(request.getBirthDate());

        // Step 4: Create Student linked to Academic Year with UNPAID status
        Student student = Student.builder()
                .person(childPerson)
                .batch(academicYear)
                .ageGroup(ageGroup)
                .registerDate(LocalDate.now())
                .status(StudentStatus.UNPAID)
                .paymentStatus("PENDING")
                .build();
        studentRepository.save(student);
        log.info("Created Student entry with ID: {} linked to Academic Year: {}", student.getId(),
                academicYear.getName());

        // Create Father Parent
        Parent father = Parent.builder()
                .id(UUID.randomUUID().toString())
                .student(student)
                .relation(Relation.AYAH)
                .name(request.getFatherName())
                .job(request.getParentJob())
                .handphone(request.getPhoneNumber())
                .email(request.getEmail())
                .build();
        parentRepository.save(father);

        // Create Mother Parent
        Parent mother = Parent.builder()
                .id(UUID.randomUUID().toString())
                .student(student)
                .relation(Relation.IBU)
                .name(request.getMotherName())
                .build();
        parentRepository.save(mother);

        log.info("Created Parent entries for student: {}", student.getId());

        // Step 3: Create Xendit Invoice with dynamic fee
        String invoiceUrl = null;
        try {
            var invoice = paymentService.createInvoice(student, father, registrationFee);

            // Update student with payment info
            student.setXenditInvoiceId(invoice.getId());
            student.setPaymentUrl(invoice.getInvoiceUrl());
            studentRepository.save(student);

            invoiceUrl = invoice.getInvoiceUrl();
            log.info("Created Xendit Invoice: {} for student: {} with fee: Rp {}",
                    invoice.getId(), student.getId(), registrationFee);
        } catch (Exception e) {
            log.error("Failed to create Xendit invoice: {}", e.getMessage());
            // Don't fail registration, allow retry later
        }

        return RegisterResponse.builder()
                .studentId(student.getId())
                .studentName(childPerson.getFullName())
                .status(StudentStatus.UNPAID.name())
                .invoiceUrl(invoiceUrl)
                .build();
    }

    private AgeGroup calculateAgeGroup(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age >= 2 && age < 3) {
            return AgeGroup.TWO_TO_THREE;
        } else if (age >= 3 && age < 4) {
            return AgeGroup.THREE_TO_FOUR;
        } else if (age >= 4 && age < 5) {
            return AgeGroup.FOUR_TO_FIVE;
        } else if (age >= 5 && age <= 6) {
            return AgeGroup.FIVE_TO_SIX;
        } else {
            if (age < 2) {
                return AgeGroup.TWO_TO_THREE;
            } else {
                return AgeGroup.FIVE_TO_SIX;
            }
        }
    }
}
