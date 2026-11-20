package model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Reusable Payroll Template (Prototype Pattern).
 *
 * Captures a reusable payroll configuration that can be cloned
 * and customized per employee without re-specifying every field.
 */
public class PayrollTemplate implements Cloneable {

    private String templateName;
    private EmployeeType employeeType;
    private SalaryComponents salaryComponents;
    private Map<String, BigDecimal> deductions;
    private String bonusDetails;

    public PayrollTemplate(String templateName, EmployeeType employeeType,
                           SalaryComponents salaryComponents) {
        this.templateName = Objects.requireNonNull(templateName, "Template name must not be null");
        this.employeeType = Objects.requireNonNull(employeeType, "Employee type must not be null");
        this.salaryComponents = new SalaryComponents(Objects.requireNonNull(salaryComponents, "Salary components must not be null"));
        this.deductions = new LinkedHashMap<>();
    }

    /**
     * Prototype pattern method: creates a deep copy of this template.
     */
    @Override
    public PayrollTemplate clone() {
        try {
            PayrollTemplate copy = (PayrollTemplate) super.clone();
            copy.salaryComponents = new SalaryComponents(this.salaryComponents);
            copy.deductions = new LinkedHashMap<>(this.deductions);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public String getTemplateName() {
        return templateName;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public SalaryComponents getSalaryComponents() {
        return new SalaryComponents(salaryComponents);
    }

    public Map<String, BigDecimal> getDeductions() {
        return Collections.unmodifiableMap(deductions);
    }

    public String getBonusDetails() {
        return bonusDetails;
    }

    public void setTemplateName(String templateName) {
        this.templateName = Objects.requireNonNull(templateName, "Template name must not be null");
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = Objects.requireNonNull(employeeType, "Employee type must not be null");
    }

    public void setSalaryComponents(SalaryComponents salaryComponents) {
        this.salaryComponents = new SalaryComponents(
                Objects.requireNonNull(salaryComponents, "Salary components must not be null")
        );
    }

    public void addDeduction(String name, BigDecimal amount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deduction name must not be blank");
        }
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Deduction amount must be non-negative");
        }
        this.deductions.put(name, amount);
    }

    public void setBonusDetails(String bonusDetails) {
        this.bonusDetails = bonusDetails;
    }

    @Override
    public String toString() {
        return String.format(
                "PayrollTemplate{name='%s', type=%s, components=%s, deductions=%s, bonus='%s'}",
                templateName, employeeType, salaryComponents, deductions, bonusDetails);
    }
}
