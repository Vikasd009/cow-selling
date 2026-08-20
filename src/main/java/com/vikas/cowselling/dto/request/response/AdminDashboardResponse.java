package com.vikas.cowselling.dto.request.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalSellers;
    private long totalCows;
    private long pendingCows;
    private long availableCows;
    private long soldCows;
    private long rejectedCows;
}
