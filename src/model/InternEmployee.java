package model;

import tax.DeductionPolicy;
import tax.TaxPolicy;
import tax.TaxRegime;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Interns.
 * All monetary values use BigDecimal to avoid floating-point errors.
 * <p>
 * Tax and deduction rules are delegated to {@link tax.InternTaxPolicy}
 * and {@link tax.InternDeductionPolicy} for reuse across calculation paths.
 * <p>
 * Gross salary calculation: monthly stipend (matches {@link calculator.InternSalaryCalculator}).
 */
public class InternEmployee extends Employee {

    private BigDecimal stipend;

    public InternEmployee(int id, String name, String email, String department, String bankAccountNumber, BigDecimal stipend) {
        this(id, name, email, department, bankAccountNumber, stipend, TaxRegime.NEW);
    }

    public InternEmployee(int id, String name, String email, String department, String bankAccountNumber, BigDecimal stipend, TaxRegime taxRegime) {
        super(id, name, email, department, bankAccountNumber, taxRegime);
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
        return GrossPayCalculator.intern(stipend);
    }

    @Override
    public BigDecimal calculateTax() {
        return TaxPolicy.forEmployee(this).calculateTax(calculateGrossPay(), getTaxRegime());
    }

    @Override
    public BigDecimal calculateDeductions() {
        return DeductionPolicy.forEmployee(this).calculateDeductions(calculateGrossPay());
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
