package creational.prototype;

import creational.factorymethod.EmployeeType;
import creational.factorymethod.SalaryComponents;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Task 5: Prototype Pattern - Reusable Payroll Template.
 *
 * A template captures a reusable payroll configuration that can be
 * cloned and customized per employee without re-specifying every field.
 *
 * Deep-copy semantics: cloning produces independent copies of all
 * mutable nested objects (SalaryComponents, deductions map), so
 * modifying the clone never affects the original template.
 */
public class PayrollTemplate implements Cloneable {

    private String templateName;
    private EmployeeType employeeType;
    private SalaryComponents salaryComponents;
    private Map<String, Double> deductions;
    private String bonusDetails;

    public PayrollTemplate(String templateName, EmployeeType employeeType,
                           SalaryComponents salaryComponents) {
        this.templateName = Objects.requireNonNull(templateName, "Template name must not be null");
        this.employeeType = Objects.requireNonNull(employeeType, "Employee type must not be null");
        this.salaryComponents = Objects.requireNonNull(salaryComponents, "Salary components must not be null");
        this.deductions = new LinkedHashMap<>();
    }

    /**
     * Prototype method: creates a deep copy of this template.
     * Mutable nested objects are copied so the clone is fully independent.
     */
    @Override
    public PayrollTemplate clone() {
        try {
            PayrollTemplate copy = (PayrollTemplate) super.clone();
            copy.salaryComponents = new SalaryComponents(this.salaryComponents);
            copy.deductions = new LinkedHashMap<>(this.deductions);
            return copy;
        } catch (CloneNotSupportedException e) {
            // Cannot happen: this class implements Cloneable
            throw new AssertionError(e);
        }
    }

    // Getters
    public String getTemplateName() {
        return templateName;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public SalaryComponents getSalaryComponents() {
        return salaryComponents;
    }

    public Map<String, Double> getDeductions() {
        return Collections.unmodifiableMap(deductions);
    }

    public String getBonusDetails() {
        return bonusDetails;
    }

    // Mutators (used to customize a cloned template)
    public void setTemplateName(String templateName) {
        this.templateName = Objects.requireNonNull(templateName, "Template name must not be null");
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = Objects.requireNonNull(employeeType, "Employee type must not be null");
    }

    public void setSalaryComponents(SalaryComponents salaryComponents) {
        this.salaryComponents = Objects.requireNonNull(salaryComponents, "Salary components must not be null");
    }

    public void addDeduction(String name, double amount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deduction name must not be blank");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Deduction amount cannot be negative: " + amount);
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
