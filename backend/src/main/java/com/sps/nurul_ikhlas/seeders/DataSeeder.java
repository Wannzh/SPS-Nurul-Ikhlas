package com.sps.nurul_ikhlas.seeders;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sps.nurul_ikhlas.models.entities.AcademicYear;
import com.sps.nurul_ikhlas.models.entities.People;
import com.sps.nurul_ikhlas.models.entities.SchoolClass;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.models.enums.AcademicYearStatus;
import com.sps.nurul_ikhlas.models.enums.Gender;
import com.sps.nurul_ikhlas.models.enums.Role;
import com.sps.nurul_ikhlas.repositories.AcademicYearRepository;
import com.sps.nurul_ikhlas.repositories.PeopleRepository;
import com.sps.nurul_ikhlas.repositories.SchoolClassRepository;
import com.sps.nurul_ikhlas.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PeopleRepository peopleRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedAcademicYear();
        seedSchoolClasses();
    }

    private void seedAdminUser() {
        if (userRepository.existsByUsername("admin")) {
            log.info("Admin user already exists. Skipping...");
            return;
        }

        log.info("Creating default admin user...");

        // Create People entry for Admin
        People adminPerson = People.builder()
                .id(UUID.randomUUID().toString())
                .fullName("Administrator")
                .gender(Gender.FEMALE)
                .build();
        peopleRepository.save(adminPerson);

        // Create User entry
        User adminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .person(adminPerson)
                .build();
        userRepository.save(adminUser);

        log.info("Admin user created successfully.");
    }

    private void seedAcademicYear() {
        if (academicYearRepository.count() > 0) {
            log.info("Academic years already exist. Skipping...");
            return;
        }

        log.info("Creating default academic year...");

        AcademicYear academicYear = AcademicYear.builder()
                .id(UUID.randomUUID().toString())
                .name("2024/2025")
                .status(AcademicYearStatus.OPEN)
                .registrationFee(150000.0)
                .build();
        academicYearRepository.save(academicYear);

        log.info("Academic year '2024/2025' created successfully.");
    }

    private void seedSchoolClasses() {
        if (schoolClassRepository.count() > 0) {
            log.info("School classes already exist. Skipping...");
            return;
        }

        log.info("Creating default school classes...");

        List<SchoolClass> classes = List.of(
                SchoolClass.builder()
                        .id(UUID.randomUUID().toString())
                        .name("TK A")
                        .quota(20)
                        .build(),
                SchoolClass.builder()
                        .id(UUID.randomUUID().toString())
                        .name("TK B")
                        .quota(20)
                        .build());

        schoolClassRepository.saveAll(classes);
        log.info("School classes 'TK A' and 'TK B' created successfully.");
    }
}
