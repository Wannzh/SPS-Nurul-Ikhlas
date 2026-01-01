package com.sps.nurul_ikhlas.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sps.nurul_ikhlas.models.entities.District;
import com.sps.nurul_ikhlas.models.entities.Province;
import com.sps.nurul_ikhlas.models.entities.Regency;
import com.sps.nurul_ikhlas.models.entities.Village;
import com.sps.nurul_ikhlas.repositories.DistrictRepository;
import com.sps.nurul_ikhlas.repositories.ProvinceRepository;
import com.sps.nurul_ikhlas.repositories.RegencyRepository;
import com.sps.nurul_ikhlas.repositories.VillageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WilayahService {

    private final ProvinceRepository provinceRepository;
    private final RegencyRepository regencyRepository;
    private final DistrictRepository districtRepository;
    private final VillageRepository villageRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String API_BASE_URL = "https://emsifa.github.io/api-wilayah-indonesia/api";

    /**
     * Get all provinces from local database.
     */
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    /**
     * Get regencies by province ID.
     * First checks local DB, if empty fetches from external API and saves.
     */
    public List<Regency> getRegenciesByProvinceId(String provinceId) {
        List<Regency> regencies = regencyRepository.findByProvinceId(provinceId);

        if (regencies.isEmpty()) {
            log.info("Regencies for province {} not found in DB. Fetching from external API...", provinceId);
            regencies = fetchAndSaveRegencies(provinceId);
        }

        return regencies;
    }

    /**
     * Get districts by regency ID.
     * First checks local DB, if empty fetches from external API and saves.
     */
    public List<District> getDistrictsByRegencyId(String regencyId) {
        List<District> districts = districtRepository.findByRegencyId(regencyId);

        if (districts.isEmpty()) {
            log.info("Districts for regency {} not found in DB. Fetching from external API...", regencyId);
            districts = fetchAndSaveDistricts(regencyId);
        }

        return districts;
    }

    /**
     * Get villages by district ID.
     * First checks local DB, if empty fetches from external API and saves.
     */
    public List<Village> getVillagesByDistrictId(String districtId) {
        List<Village> villages = villageRepository.findByDistrictId(districtId);

        if (villages.isEmpty()) {
            log.info("Villages for district {} not found in DB. Fetching from external API...", districtId);
            villages = fetchAndSaveVillages(districtId);
        }

        return villages;
    }

    private List<Regency> fetchAndSaveRegencies(String provinceId) {
        try {
            String url = API_BASE_URL + "/regencies/" + provinceId + ".json";
            String response = restTemplate.getForObject(url, String.class);

            List<Map<String, String>> regenciesData = objectMapper.readValue(
                    response,
                    new TypeReference<List<Map<String, String>>>() {
                    });

            Province province = provinceRepository.findById(provinceId)
                    .orElseThrow(() -> new RuntimeException("Province not found: " + provinceId));

            List<Regency> regencies = regenciesData.stream()
                    .map(data -> Regency.builder()
                            .id(data.get("id"))
                            .name(data.get("name"))
                            .province(province)
                            .build())
                    .toList();

            regencyRepository.saveAll(regencies);
            log.info("Saved {} regencies for province {}", regencies.size(), provinceId);

            return regencies;

        } catch (Exception e) {
            log.error("Failed to fetch regencies for province {}: {}", provinceId, e.getMessage());
            return List.of();
        }
    }

    private List<District> fetchAndSaveDistricts(String regencyId) {
        try {
            String url = API_BASE_URL + "/districts/" + regencyId + ".json";
            String response = restTemplate.getForObject(url, String.class);

            List<Map<String, String>> districtsData = objectMapper.readValue(
                    response,
                    new TypeReference<List<Map<String, String>>>() {
                    });

            Regency regency = regencyRepository.findById(regencyId)
                    .orElseThrow(() -> new RuntimeException("Regency not found: " + regencyId));

            List<District> districts = districtsData.stream()
                    .map(data -> District.builder()
                            .id(data.get("id"))
                            .name(data.get("name"))
                            .regency(regency)
                            .build())
                    .toList();

            districtRepository.saveAll(districts);
            log.info("Saved {} districts for regency {}", districts.size(), regencyId);

            return districts;

        } catch (Exception e) {
            log.error("Failed to fetch districts for regency {}: {}", regencyId, e.getMessage());
            return List.of();
        }
    }

    private List<Village> fetchAndSaveVillages(String districtId) {
        try {
            String url = API_BASE_URL + "/villages/" + districtId + ".json";
            String response = restTemplate.getForObject(url, String.class);

            List<Map<String, String>> villagesData = objectMapper.readValue(
                    response,
                    new TypeReference<List<Map<String, String>>>() {
                    });

            District district = districtRepository.findById(districtId)
                    .orElseThrow(() -> new RuntimeException("District not found: " + districtId));

            List<Village> villages = villagesData.stream()
                    .map(data -> Village.builder()
                            .id(data.get("id"))
                            .name(data.get("name"))
                            .district(district)
                            .build())
                    .toList();

            villageRepository.saveAll(villages);
            log.info("Saved {} villages for district {}", villages.size(), districtId);

            return villages;

        } catch (Exception e) {
            log.error("Failed to fetch villages for district {}: {}", districtId, e.getMessage());
            return List.of();
        }
    }
}
