package model;

import tax.TaxRegime;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Abstract Base Class for all Employees.
 * Adheres to:
 * - Single Responsibility Principle (SRP): Encapsulates core employee profile state & contracts.
 * - Open-Closed Principle (OCP): New employee categories extend this class without modifying existing code.
 * - Liskov Substitution Principle (LSP): Subclasses provide valid implementations for all abstract contracts.
 */
public abstract class Employee {
    private final int id;
    private String name;
    private String email;
    private String department;
    private String bankAccountNumber;
    private TaxRegime taxRegime;

    protected Employee(int id, String name, String email, String department, String bankAccountNumber) {
        this(id, name, email, department, bankAccountNumber, TaxRegime.NEW);
    }

    protected Employee(int id, String name, String email, String department, String bankAccountNumber, TaxRegime taxRegime) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive: " + id);
        }
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.email = Objects.requireNonNull(email, "Email must not be null");
        this.department = Objects.requireNonNull(department, "Department must not be null");
        this.bankAccountNumber = Objects.requireNonNull(bankAccountNumber, "Bank account must not be null");
        this.taxRegime = taxRegime != null ? taxRegime : TaxRegime.NEW;
    }

    // Getters & Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name must not be null");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email must not be null");
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = Objects.requireNonNull(department, "Department must not be null");
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = Objects.requireNonNull(bankAccountNumber, "Bank account must not be null");
    }

    public TaxRegime getTaxRegime() {
        return taxRegime;
    }

    public void setTaxRegime(TaxRegime taxRegime) {
        this.taxRegime = taxRegime != null ? taxRegime : TaxRegime.NEW;
    }

    // Abstract polymorphic contracts
    // All monetary values use BigDecimal to avoid floating-point errors.
    public abstract BigDecimal calculateGrossPay();
    public abstract BigDecimal calculateTax();
    public abstract BigDecimal calculateDeductions();
    public abstract String getRoleCategory();
    public abstract int getTypeCode();
    public abstract String getPayDetailsSummary();

    /**
     * Returns the unified EmployeeType category for this employee.
     */
    public EmployeeType getEmployeeType() {
        return EmployeeType.fromTypeCode(getTypeCode());
    }

    /**
     * Template method: Generates an immutable PaySlip by coordinating polymorphic calculations.
     * Guarantees DRY (Don't Repeat Yourself) principle across the application.
     */
    public PaySlip generatePaySlip() {
        BigDecimal gross = calculateGrossPay();
        BigDecimal tax = calculateTax();
        BigDecimal deductions = calculateDeductions();
        BigDecimal net = gross.subtract(tax).subtract(deductions);
        return new PaySlip(id, name, department, getRoleCategory(), bankAccountNumber, gross, tax, deductions, net);
    }
}
