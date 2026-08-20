package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.response.AdminDashboardResponse;
import com.vikas.cowselling.dto.request.response.CowResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.dto.request.response.UserResponse;

public interface AdminService {

    PageResponse<CowResponse> getPendingCows(int page, int size);

    CowResponse approveCow(Long cowId);

    CowResponse rejectCow(Long cowId, String reason);

    PageResponse<UserResponse> getAllUsers(int page, int size);

    UserResponse blockUser(Long userId);

    UserResponse unblockUser(Long userId);

    AdminDashboardResponse getDashboardStatistics();

}
