package model.value;

import java.math.BigDecimal;

/**
 * Value Object / DTO representing the outcome of a payroll calculation for an employee.
 * Immutable to ensure thread safety and data integrity.
 * All monetary values use BigDecimal to avoid floating-point errors.
 *
 * Captures request-specific financial year and descriptive bonus details.
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
        BigDecimal netPay,
        String financialYear,
        String bonusDetails
) {
    public static final String DEFAULT_FINANCIAL_YEAR = "FY2025-26";

    public PaySlip {
        if (employeeName == null || employeeName.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be null or blank");
        }
        if (financialYear == null || financialYear.isBlank()) {
            financialYear = DEFAULT_FINANCIAL_YEAR;
        }
    }

    /**
     * Overloaded constructor for backward compatibility.
     * Uses default financial year and null bonusDetails.
     */
    public PaySlip(int employeeId, String employeeName, String department, String roleCategory,
                   String bankAccountNumber, BigDecimal grossPay, BigDecimal taxWithholding,
                   BigDecimal deductions, BigDecimal netPay) {
        this(employeeId, employeeName, department, roleCategory, bankAccountNumber,
                grossPay, taxWithholding, deductions, netPay,
                DEFAULT_FINANCIAL_YEAR, null);
    }

    /**
     * Overloaded constructor with financialYear but no bonusDetails.
     */
    public PaySlip(int employeeId, String employeeName, String department, String roleCategory,
                   String bankAccountNumber, BigDecimal grossPay, BigDecimal taxWithholding,
                   BigDecimal deductions, BigDecimal netPay, String financialYear) {
        this(employeeId, employeeName, department, roleCategory, bankAccountNumber,
                grossPay, taxWithholding, deductions, netPay,
                financialYear, null);
    }
}
