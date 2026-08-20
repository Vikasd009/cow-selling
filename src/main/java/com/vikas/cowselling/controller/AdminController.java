package com.vikas.cowselling.controller;

import com.vikas.cowselling.dto.request.RejectCowRequest;
import com.vikas.cowselling.dto.request.response.AdminDashboardResponse;
import com.vikas.cowselling.dto.request.response.CowResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.dto.request.response.UserResponse;
import com.vikas.cowselling.service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(
            AdminService adminService
    ) {
        this.adminService = adminService;
    }

    @GetMapping("/cows/pending")
    public ResponseEntity<PageResponse<CowResponse>>
    getPendingCows(

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(50)
            int size
    ) {

        return ResponseEntity.ok(
                adminService.getPendingCows(
                        page,
                        size
                )
        );
    }

    @PatchMapping("/cows/{cowId}/approve")
    public ResponseEntity<CowResponse> approveCow(
            @PathVariable Long cowId
    ) {

        return ResponseEntity.ok(
                adminService.approveCow(cowId)
        );
    }

    @PatchMapping("/cows/{cowId}/reject")
    public ResponseEntity<CowResponse> rejectCow(
            @PathVariable Long cowId,
            @Valid @RequestBody RejectCowRequest request
    ) {
        return ResponseEntity.ok(
                adminService.rejectCow(
                        cowId,
                        request.getReason()
                )
        );
    }


    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserResponse>>
    getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size
    ) {
        return ResponseEntity.ok(
                adminService.getAllUsers(
                        page,
                        size
                )
        );
    }

    @PatchMapping("/users/{userId}/block")
    public ResponseEntity<UserResponse> blockUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                adminService.blockUser(userId)
        );
    }

    @PatchMapping("/users/{userId}/unblock")
    public ResponseEntity<UserResponse> unblockUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                adminService.unblockUser(userId)
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse>
    getDashboardStatistics() {
        return ResponseEntity.ok(
                adminService.getDashboardStatistics()
        );
    }


}

