package com.sps.nurul_ikhlas.services;

import com.sps.nurul_ikhlas.payload.response.ParentDashboardResponse;

public interface ParentService {
    ParentDashboardResponse getMyData(String username);
}
