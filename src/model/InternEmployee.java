package model;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Interns.
 * All monetary values use BigDecimal to avoid floating-point errors.
 */
public class InternEmployee extends Employee {
    private static final BigDecimal TAX_EXEMPT_STIPEND_LIMIT = new BigDecimal("2000.00");
    private static final BigDecimal STIPEND_SURTAX_RATE = new BigDecimal("0.05");

    private BigDecimal stipend;

    public InternEmployee(int id, String name, String email, String department, String bankAccountNumber, BigDecimal stipend) {
        super(id, name, email, department, bankAccountNumber);
        if (stipend == null || stipend.signum() < 0) {
            throw new IllegalArgumentException("Stipend must be non-negative");
        }
        this.stipend = stipend;
    }

    public BigDecimal getStipend() {
        return stipend;
    }

    public void setStipend(BigDecimal stipend) {
        if (stipend == null || stipend.signum() < 0) {
            throw new IllegalArgumentException("Stipend must be non-negative");
        }
        this.stipend = stipend;
    }

    @Override
    public BigDecimal calculateGrossPay() {
        return stipend;
    }

    @Override
    public BigDecimal calculateTax() {
        if (stipend.compareTo(TAX_EXEMPT_STIPEND_LIMIT) > 0) {
            return stipend.subtract(TAX_EXEMPT_STIPEND_LIMIT).multiply(STIPEND_SURTAX_RATE);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateDeductions() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getRoleCategory() {
        return "Intern";
    }

    @Override
    public int getTypeCode() {
        return 4;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("Stipend: $%.2f", stipend);
    }
}
