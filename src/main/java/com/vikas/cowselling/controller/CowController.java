package com.vikas.cowselling.controller;

import com.vikas.cowselling.dto.request.CreateCowRequest;
import com.vikas.cowselling.dto.request.UpdateCowRequest;
import com.vikas.cowselling.dto.request.response.CowResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.service.CowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/cows")
public class CowController {

    private final CowService cowService;

    public CowController(CowService cowService){
        this.cowService = cowService;
    }

    @PostMapping
    public ResponseEntity<CowResponse> createCow(@Valid @RequestBody CreateCowRequest request,
                                                 Authentication authentication){
        CowResponse response = cowService.createCow( request, authentication.getName() );
        return ResponseEntity .status(HttpStatus.CREATED) .body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<CowResponse>>
    getAllAvailableCows(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page cannot be negative") int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 50, message = "Size cannot exceed 50")
            int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String search
    ) {

        return ResponseEntity.ok(
                cowService.getAllAvailableCows(
                        page,
                        size,
                        sortBy,
                        direction,
                        breed,
                        minPrice,
                        maxPrice,
                        city,
                        state,
                        search
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<CowResponse> getCowById(@PathVariable Long id ) {
        return ResponseEntity.ok( cowService.getCowById(id) );
    }

    @GetMapping("/my-cows")
    public ResponseEntity<List<CowResponse>> getMyCows(Authentication authentication ) {
        return ResponseEntity.ok( cowService.getMyCows( authentication.getName() ) );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CowResponse> updateCow(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCowRequest request,
            Authentication authentication
    ) {

        CowResponse response = cowService.updateCow(id, request, authentication.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCow(
            @PathVariable Long id,
            Authentication authentication
    ) {
        cowService.deleteCow(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/sold")
    public ResponseEntity<CowResponse> markCowAsSold(
            @PathVariable Long id,
            Authentication authentication
    ) {
        CowResponse response = cowService.markCowAsSold(id, authentication.getName());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/resubmit")
    public ResponseEntity<CowResponse> resubmitCow(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cowService.resubmitCow(
                        id,
                        authentication.getName()
                )
        );
    }

}
