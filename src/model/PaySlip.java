package model;

import java.math.BigDecimal;

/**
 * Value Object / DTO representing the outcome of a payroll calculation for an employee.
 * Immutable to ensure thread safety and data integrity.
 * All monetary values use BigDecimal to avoid floating-point errors.
 */
public record PaySlip(
        int employeeId,
        String employeeName,
        String department,
        String roleCategory,
        String bankAccountNumber,
        BigDecimal grossPay,
        BigDecimal taxWithholding,
        BigDecimal deductions,
        BigDecimal netPay
) {
    public PaySlip {
        if (employeeName == null || employeeName.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be null or blank");
        }
    }
}
