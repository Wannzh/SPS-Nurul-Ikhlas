package com.sps.nurul_ikhlas.services;

import java.util.List;

import com.sps.nurul_ikhlas.models.entities.District;
import com.sps.nurul_ikhlas.models.entities.Province;
import com.sps.nurul_ikhlas.models.entities.Regency;
import com.sps.nurul_ikhlas.models.entities.Village;

public interface WilayahService {
    List<Province> getAllProvinces();

    List<Regency> getRegenciesByProvinceId(String provinceId);

    List<District> getDistrictsByRegencyId(String regencyId);

    List<Village> getVillagesByDistrictId(String districtId);
}
