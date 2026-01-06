package com.sps.nurul_ikhlas.services;

import com.sps.nurul_ikhlas.payload.request.RegisterRequest;
import com.sps.nurul_ikhlas.payload.request.SetupPasswordRequest;
import com.sps.nurul_ikhlas.payload.response.RegisterResponse;

public interface AuthService {
    RegisterResponse registerStudent(RegisterRequest request);

    String setupPassword(SetupPasswordRequest request);
}
