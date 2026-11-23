package model.request;

import model.entity.EmployeeType;
import model.value.SalaryComponents;
import tax.regime.TaxRegime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable PayrollRequest constructed via the Builder Pattern.
 * Describes a payroll run for one employee with defensive copying.
 */
public final class PayrollRequest {

    private final int employeeId;
    private final String employeeName;
    private final String department;
    private final String bankAccountNumber;
    private final EmployeeType employeeType;
    private final String financialYear;
    private final TaxRegime taxRegime;
    private final SalaryComponents salaryComponents;
    private final Map<String, BigDecimal> deductions;
    private final String bonusDetails;

    private PayrollRequest(Builder builder) {
        this.employeeId = builder.employeeId;
        this.employeeName = builder.employeeName;
        this.department = builder.department;
        this.bankAccountNumber = builder.bankAccountNumber;
        this.employeeType = builder.employeeType;
        this.financialYear = builder.financialYear;
        this.taxRegime = builder.taxRegime;
        this.salaryComponents = new SalaryComponents(builder.salaryComponents);
        this.deductions = Collections.unmodifiableMap(new LinkedHashMap<>(builder.deductions));
        this.bonusDetails = builder.bonusDetails;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public TaxRegime getTaxRegime() {
        return taxRegime;
    }

    public SalaryComponents getSalaryComponents() {
        return new SalaryComponents(salaryComponents);
    }

    public Map<String, BigDecimal> getDeductions() {
        return deductions;
    }

    public String getBonusDetails() {
        return bonusDetails;
    }

    public BigDecimal totalOptionalDeductions() {
        return deductions.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return String.format(
                "PayrollRequest{employeeId=%d, name='%s', dept='%s', type=%s, fy='%s', regime=%s, components=%s, deductions=%s, bonus='%s'}",
                employeeId, employeeName, department, employeeType, financialYear, taxRegime,
                salaryComponents, deductions, bonusDetails);
    }

    /**
     * Builder Pattern: constructs a validated, immutable PayrollRequest.
     */
    public static class Builder {
        private int employeeId;
        private String employeeName;
        private String department;
        private String bankAccountNumber = "N/A";
        private EmployeeType employeeType;
        private String financialYear;
        private TaxRegime taxRegime;
        private SalaryComponents salaryComponents;
        private final Map<String, BigDecimal> deductions = new LinkedHashMap<>();
        private String bonusDetails;

        public Builder employeeId(int employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeName(String employeeName) {
            this.employeeName = employeeName;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder bankAccountNumber(String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
            return this;
        }

        public Builder employeeType(EmployeeType employeeType) {
            this.employeeType = employeeType;
            return this;
        }

        public Builder financialYear(String financialYear) {
            this.financialYear = financialYear;
            return this;
        }

        public Builder taxRegime(TaxRegime taxRegime) {
            this.taxRegime = taxRegime;
            return this;
        }

        public Builder salaryComponents(SalaryComponents salaryComponents) {
            this.salaryComponents = salaryComponents != null ? new SalaryComponents(salaryComponents) : null;
            return this;
        }

        public Builder addDeduction(String name, BigDecimal amount) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Deduction name must not be blank");
            }
            if (amount == null || amount.signum() < 0) {
                throw new IllegalArgumentException("Deduction amount must be non-negative");
            }
            this.deductions.put(name, amount);
            return this;
        }

        public Builder bonusDetails(String bonusDetails) {
            this.bonusDetails = bonusDetails;
            return this;
        }

        public PayrollRequest build() {
            if (employeeId <= 0) {
                throw new IllegalStateException("Employee ID must be a positive integer");
            }
            if (employeeName == null || employeeName.isBlank()) {
                throw new IllegalStateException("Employee name is required");
            }
            if (department == null || department.isBlank()) {
                throw new IllegalStateException("Department is required");
            }
            if (employeeType == null) {
                throw new IllegalStateException("Employee type is required");
            }
            if (financialYear == null || financialYear.isBlank()) {
                throw new IllegalStateException("Financial year is required");
            }
            if (taxRegime == null) {
                throw new IllegalStateException("Tax regime is required");
            }
            if (salaryComponents == null) {
                throw new IllegalStateException("Salary components are required");
            }
            return new PayrollRequest(this);
        }
    }
}
