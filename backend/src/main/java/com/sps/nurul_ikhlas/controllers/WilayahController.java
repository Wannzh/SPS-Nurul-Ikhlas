package com.sps.nurul_ikhlas.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.nurul_ikhlas.models.entities.District;
import com.sps.nurul_ikhlas.models.entities.Province;
import com.sps.nurul_ikhlas.models.entities.Regency;
import com.sps.nurul_ikhlas.models.entities.Village;
import com.sps.nurul_ikhlas.services.WilayahService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wilayah")
@RequiredArgsConstructor
public class WilayahController {

    private final WilayahService wilayahService;

    /**
     * Get all provinces from local database.
     */
    @GetMapping("/provinces")
    public ResponseEntity<List<Province>> getAllProvinces() {
        List<Province> provinces = wilayahService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    /**
     * Get regencies by province ID.
     * Lazy-fetches from external API if not in local DB.
     */
    @GetMapping("/regencies/{provinceId}")
    public ResponseEntity<List<Regency>> getRegenciesByProvinceId(@PathVariable String provinceId) {
        List<Regency> regencies = wilayahService.getRegenciesByProvinceId(provinceId);
        return ResponseEntity.ok(regencies);
    }

    /**
     * Get districts by regency ID.
     * Lazy-fetches from external API if not in local DB.
     */
    @GetMapping("/districts/{regencyId}")
    public ResponseEntity<List<District>> getDistrictsByRegencyId(@PathVariable String regencyId) {
        List<District> districts = wilayahService.getDistrictsByRegencyId(regencyId);
        return ResponseEntity.ok(districts);
    }

    /**
     * Get villages by district ID.
     * Lazy-fetches from external API if not in local DB.
     */
    @GetMapping("/villages/{districtId}")
    public ResponseEntity<List<Village>> getVillagesByDistrictId(@PathVariable String districtId) {
        List<Village> villages = wilayahService.getVillagesByDistrictId(districtId);
        return ResponseEntity.ok(villages);
    }
}
