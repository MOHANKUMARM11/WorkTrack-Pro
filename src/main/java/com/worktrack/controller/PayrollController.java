package com.worktrack.controller;

import com.worktrack.constants.PayrollStatus;
import com.worktrack.dto.request.PayrollRequest;
import com.worktrack.dto.response.PayrollResponse;
import com.worktrack.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping
    public ResponseEntity<PayrollResponse> createPayroll(
            @Valid @RequestBody PayrollRequest request) {

        return new ResponseEntity<>(
                payrollService.createPayroll(request),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponse> getPayrollById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.getPayrollById(id));
    }

    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {

        return ResponseEntity.ok(
                payrollService.getAllPayrolls());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayrollResponse> updatePayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollRequest request) {

        return ResponseEntity.ok(
                payrollService.updatePayroll(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PayrollResponse> updatePayrollStatus(
            @PathVariable Long id,
            @RequestParam PayrollStatus status) {

        return ResponseEntity.ok(
                payrollService.updatePayrollStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(
            @PathVariable Long id) {

        payrollService.deletePayroll(id);

        return ResponseEntity.noContent().build();
    }
}