package com.sps.nurul_ikhlas.services;

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
import com.sps.nurul_ikhlas.payload.RegisterRequest;
import com.sps.nurul_ikhlas.payload.RegisterResponse;
import com.sps.nurul_ikhlas.repositories.AcademicYearRepository;
import com.sps.nurul_ikhlas.repositories.ParentRepository;
import com.sps.nurul_ikhlas.repositories.PeopleRepository;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.VillageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final PeopleRepository peopleRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final VillageRepository villageRepository;
    private final AcademicYearRepository academicYearRepository;

    @Transactional
    public RegisterResponse registerStudent(RegisterRequest request) {
        // Validate agreement
        if (!Boolean.TRUE.equals(request.getIsAgreed())) {
            throw new RuntimeException("Anda harus menyetujui persyaratan pendaftaran");
        }

        // Validate phone number not already used
        if (parentRepository.existsByHandphone(request.getPhoneNumber())) {
            throw new RuntimeException("Nomor telepon sudah terdaftar. Silakan gunakan nomor lain atau hubungi admin.");
        }

        // Fetch Village for address
        Village village = villageRepository.findById(request.getVillageId())
                .orElseThrow(() -> new RuntimeException("Desa/Kelurahan tidak ditemukan"));

        // Fetch OPEN Academic Year
        AcademicYear academicYear = academicYearRepository.findByStatus(AcademicYearStatus.OPEN)
                .orElseThrow(
                        () -> new RuntimeException("Tidak ada tahun ajaran yang aktif. Pendaftaran belum dibuka."));

        // Create People (Child)
        People childPerson = People.builder()
                .id(UUID.randomUUID().toString())
                .fullName(request.getChildFullName())
                .nickname(request.getChildNickname())
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

        // Create Student
        Student student = Student.builder()
                .person(childPerson)
                .batch(academicYear)
                .ageGroup(ageGroup)
                .registerDate(LocalDate.now())
                .status(StudentStatus.REGISTERED)
                .build();
        studentRepository.save(student);
        log.info("Created Student entry with ID: {}", student.getId());

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

        return RegisterResponse.builder()
                .studentId(student.getId())
                .studentName(childPerson.getFullName())
                .status(StudentStatus.REGISTERED.name())
                .message("Pendaftaran berhasil! Silakan tunggu verifikasi dari admin.")
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
            // Default to closest group
            if (age < 2) {
                return AgeGroup.TWO_TO_THREE;
            } else {
                return AgeGroup.FIVE_TO_SIX;
            }
        }
    }
}
