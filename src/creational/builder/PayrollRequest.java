package creational.builder;

import creational.abstractfactory.TaxRegime;
import creational.factorymethod.SalaryComponents;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Task 4: Builder Pattern - Complex Object.
 *
 * Immutable PayrollRequest describing a payroll run for one employee.
 * Can only be instantiated through {@link Builder}, which enforces
 * that all required fields are present and valid before building.
 */
public final class PayrollRequest {

    private final int employeeId;
    private final String financialYear;
    private final TaxRegime taxRegime;
    private final SalaryComponents salaryComponents;
    private final Map<String, Double> deductions;
    private final String bonusDetails;

    private PayrollRequest(Builder builder) {
        this.employeeId = builder.employeeId;
        this.financialYear = builder.financialYear;
        this.taxRegime = builder.taxRegime;
        this.salaryComponents = new SalaryComponents(builder.salaryComponents);
        this.deductions = Collections.unmodifiableMap(new LinkedHashMap<>(builder.deductions));
        this.bonusDetails = builder.bonusDetails;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public TaxRegime getTaxRegime() {
        return taxRegime;
    }

    public SalaryComponents getSalaryComponents() {
        return salaryComponents;
    }

    public Map<String, Double> getDeductions() {
        return deductions;
    }

    public String getBonusDetails() {
        return bonusDetails;
    }

    @Override
    public String toString() {
        return String.format(
                "PayrollRequest{employeeId=%d, financialYear='%s', taxRegime=%s, salaryComponents=%s, deductions=%s, bonusDetails='%s'}",
                employeeId, financialYear, taxRegime, salaryComponents, deductions, bonusDetails);
    }

    /**
     * Task 4: Builder - constructs a validated, immutable PayrollRequest.
     * Required: employeeId (>0), financialYear, taxRegime, salaryComponents.
     * Optional: deductions, bonusDetails.
     */
    public static class Builder {
        private int employeeId;
        private String financialYear;
        private TaxRegime taxRegime;
        private SalaryComponents salaryComponents;
        private final Map<String, Double> deductions = new LinkedHashMap<>();
        private String bonusDetails;

        public Builder employeeId(int employeeId) {
            this.employeeId = employeeId;
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
            this.salaryComponents = salaryComponents;
            return this;
        }

        public Builder addDeduction(String name, double amount) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Deduction name must not be blank");
            }
            if (amount < 0) {
                throw new IllegalArgumentException("Deduction amount cannot be negative: " + amount);
            }
            this.deductions.put(name, amount);
            return this;
        }

        public Builder bonusDetails(String bonusDetails) {
            this.bonusDetails = bonusDetails;
            return this;
        }

        /**
         * Validates all required fields and returns an immutable PayrollRequest.
         *
         * @throws IllegalStateException if any required field is missing or invalid
         */
        public PayrollRequest build() {
            if (employeeId <= 0) {
                throw new IllegalStateException("Employee ID must be a positive integer");
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
