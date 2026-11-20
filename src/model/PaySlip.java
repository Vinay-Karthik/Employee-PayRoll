package model;

/**
 * Value Object / DTO representing the outcome of a payroll calculation for an employee.
 * Immutable to ensure thread safety and data integrity.
 */
public record PaySlip(
        int employeeId,
        String employeeName,
        String department,
        String roleCategory,
        String bankAccountNumber,
        double grossPay,
        double taxWithholding,
        double deductions,
        double netPay
) {
    public PaySlip {
        if (employeeName == null || employeeName.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be null or blank");
        }
    }
}
