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
import com.sps.nurul_ikhlas.payload.ApiResponse;
import com.sps.nurul_ikhlas.services.WilayahService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wilayah")
@RequiredArgsConstructor
public class WilayahController {

    private final WilayahService wilayahService;

    @GetMapping("/provinces")
    public ResponseEntity<ApiResponse<List<Province>>> getAllProvinces() {
        List<Province> provinces = wilayahService.getAllProvinces();
        return ResponseEntity.ok(ApiResponse.success(provinces));
    }

    @GetMapping("/regencies/{provinceId}")
    public ResponseEntity<ApiResponse<List<Regency>>> getRegenciesByProvinceId(@PathVariable String provinceId) {
        List<Regency> regencies = wilayahService.getRegenciesByProvinceId(provinceId);
        return ResponseEntity.ok(ApiResponse.success(regencies));
    }

    @GetMapping("/districts/{regencyId}")
    public ResponseEntity<ApiResponse<List<District>>> getDistrictsByRegencyId(@PathVariable String regencyId) {
        List<District> districts = wilayahService.getDistrictsByRegencyId(regencyId);
        return ResponseEntity.ok(ApiResponse.success(districts));
    }

    @GetMapping("/villages/{districtId}")
    public ResponseEntity<ApiResponse<List<Village>>> getVillagesByDistrictId(@PathVariable String districtId) {
        List<Village> villages = wilayahService.getVillagesByDistrictId(districtId);
        return ResponseEntity.ok(ApiResponse.success(villages));
    }
}
