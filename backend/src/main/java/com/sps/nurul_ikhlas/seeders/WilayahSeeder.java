package com.sps.nurul_ikhlas.seeders;

import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sps.nurul_ikhlas.models.entities.Province;
import com.sps.nurul_ikhlas.repositories.ProvinceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class WilayahSeeder implements CommandLineRunner {

    private final ProvinceRepository provinceRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String API_BASE_URL = "https://emsifa.github.io/api-wilayah-indonesia/api";

    @Override
    public void run(String... args) throws Exception {
        if (provinceRepository.count() == 0) {
            log.info("Provinces table is empty. Seeding provinces from external API...");
            seedProvinces();
        } else {
            log.info("Provinces already exist. Skipping seeding.");
        }
    }

    private void seedProvinces() {
        try {
            String url = API_BASE_URL + "/provinces.json";
            String response = restTemplate.getForObject(url, String.class);

            List<Map<String, String>> provincesData = objectMapper.readValue(
                    response,
                    new TypeReference<List<Map<String, String>>>() {
                    });

            List<Province> provinces = provincesData.stream()
                    .map(data -> Province.builder()
                            .id(data.get("id"))
                            .name(data.get("name"))
                            .build())
                    .toList();

            provinceRepository.saveAll(provinces);
            log.info("Successfully seeded {} provinces.", provinces.size());

        } catch (Exception e) {
            log.error("Failed to seed provinces: {}", e.getMessage());
        }
    }
}
