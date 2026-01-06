package com.sps.nurul_ikhlas.services.impl;

import org.springframework.stereotype.Service;

import com.sps.nurul_ikhlas.models.entities.Student;
import com.sps.nurul_ikhlas.models.entities.User;
import com.sps.nurul_ikhlas.payload.response.ParentDashboardResponse;
import com.sps.nurul_ikhlas.repositories.StudentRepository;
import com.sps.nurul_ikhlas.repositories.UserRepository;
import com.sps.nurul_ikhlas.services.ParentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParentServiceImpl implements ParentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    public ParentDashboardResponse getMyData(String username) {
        // 1. Find User by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        // 2. Get People linked to User
        if (user.getPerson() == null) {
            throw new RuntimeException("Data orang tidak terhubung dengan akun ini");
        }

        String personId = user.getPerson().getId();

        // 3. Find Student by personId
        Student student = studentRepository.findByPersonId(personId)
                .orElseThrow(() -> new RuntimeException("Data siswa tidak ditemukan untuk akun ini"));

        log.info("Fetched student data for parent: {} -> Student: {}", username, student.getPerson().getFullName());

        // 4. Build Response DTO
        return ParentDashboardResponse.builder()
                .studentId(student.getId())
                .studentName(student.getPerson().getFullName())
                .nisn(student.getNisn())
                .status(student.getStatus() != null ? student.getStatus().name() : null)
                .currentClass(student.getCurrentClass() != null ? student.getCurrentClass().getName() : null)
                .academicYear(student.getBatch() != null ? student.getBatch().getName() : null)
                .photo(student.getPhoto())
                .gender(student.getPerson().getGender() != null ? student.getPerson().getGender().name() : null)
                .birthPlace(student.getPerson().getBirthPlace())
                .birthDate(student.getPerson().getBirthDate() != null ? student.getPerson().getBirthDate().toString()
                        : null)
                .build();
    }
}
