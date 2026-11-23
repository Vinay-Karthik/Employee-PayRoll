package model;

import tax.DeductionPolicy;
import tax.TaxPolicy;
import tax.TaxRegime;

import java.math.BigDecimal;

/**
 * Concrete Employee implementation for Independent Contractors.
 * All monetary values use BigDecimal to avoid floating-point errors.
 * <p>
 * Tax and deduction rules are delegated to {@link tax.ContractorTaxPolicy}
 * and {@link tax.ContractorDeductionPolicy} for reuse across calculation paths.
 * <p>
 * Gross salary calculation: fixed contract amount when set, otherwise
 * hourly rate * hours worked (delegates to {@link GrossPayCalculator},
 * matching {@link calculator.ContractorSalaryCalculator}).
 */
public class ContractorEmployee extends Employee {

    private BigDecimal hourlyRate;
    private BigDecimal hoursWorked;
    private BigDecimal fixedContractAmount;

    public ContractorEmployee(int id, String name, String email, String department, String bankAccountNumber,
                              BigDecimal hourlyRate, BigDecimal hoursWorked) {
        this(id, name, email, department, bankAccountNumber, hourlyRate, hoursWorked, null, TaxRegime.NEW);
    }

    public ContractorEmployee(int id, String name, String email, String department, String bankAccountNumber,
                              BigDecimal hourlyRate, BigDecimal hoursWorked, TaxRegime taxRegime) {
        this(id, name, email, department, bankAccountNumber, hourlyRate, hoursWorked, null, taxRegime);
    }

    /**
     * Constructor for contractors paid a fixed contract amount.
     * Hourly rate and hours worked are not required in this mode.
     */
    public ContractorEmployee(int id, String name, String email, String department, String bankAccountNumber,
                              BigDecimal fixedContractAmount, TaxRegime taxRegime) {
        this(id, name, email, department, bankAccountNumber, null, null, fixedContractAmount, taxRegime);
    }

    private ContractorEmployee(int id, String name, String email, String department, String bankAccountNumber,
                               BigDecimal hourlyRate, BigDecimal hoursWorked, BigDecimal fixedContractAmount,
                               TaxRegime taxRegime) {
        super(id, name, email, department, bankAccountNumber, taxRegime);
        boolean hasFixedAmount = fixedContractAmount != null && fixedContractAmount.signum() > 0;
        if (hasFixedAmount) {
            this.fixedContractAmount = fixedContractAmount;
            this.hourlyRate = hourlyRate;
            this.hoursWorked = hoursWorked;
        } else {
            if (hourlyRate == null || hoursWorked == null) {
                throw new IllegalArgumentException("Hourly rate and hours worked must not be null");
            }
            if (hourlyRate.signum() < 0 || hoursWorked.signum() < 0) {
                throw new IllegalArgumentException("Hourly rate and hours worked must be non-negative");
            }
            this.hourlyRate = hourlyRate;
            this.hoursWorked = hoursWorked;
            this.fixedContractAmount = BigDecimal.ZERO;
        }
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        if (hourlyRate == null || hourlyRate.signum() < 0) {
            throw new IllegalArgumentException("Hourly rate must be non-negative");
        }
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(BigDecimal hoursWorked) {
        if (hoursWorked == null || hoursWorked.signum() < 0) {
            throw new IllegalArgumentException("Hours worked must be non-negative");
        }
        this.hoursWorked = hoursWorked;
    }

    public BigDecimal getFixedContractAmount() {
        return fixedContractAmount;
    }

    public void setFixedContractAmount(BigDecimal fixedContractAmount) {
        if (fixedContractAmount == null || fixedContractAmount.signum() < 0) {
            throw new IllegalArgumentException("Fixed contract amount must be non-negative");
        }
        this.fixedContractAmount = fixedContractAmount;
    }

    @Override
    public BigDecimal calculateGrossPay() {
        return GrossPayCalculator.contractor(fixedContractAmount, hourlyRate, hoursWorked);
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
        return "Independent Contractor";
    }

    @Override
    public EmployeeType getEmployeeType() {
        return EmployeeType.CONTRACTOR;
    }

    @Override
    public String getPayDetailsSummary() {
        if (fixedContractAmount != null && fixedContractAmount.signum() > 0) {
            return String.format("Fixed Contract: $%.2f", fixedContractAmount);
        }
        return String.format("$%.2f/hr x %.1f hrs", hourlyRate, hoursWorked);
    }
}
