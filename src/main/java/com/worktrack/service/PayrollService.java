package com.worktrack.service;

import com.worktrack.constants.PayrollStatus;
import com.worktrack.dto.request.PayrollRequest;
import com.worktrack.dto.response.PayrollResponse;

import java.util.List;

public interface PayrollService {

    PayrollResponse createPayroll(PayrollRequest request);

    PayrollResponse getPayrollById(Long id);

    List<PayrollResponse> getAllPayrolls();

    PayrollResponse updatePayroll(Long id, PayrollRequest request);

    PayrollResponse updatePayrollStatus(Long id, PayrollStatus status);

    void deletePayroll(Long id);
}