package model;

/**
 * Concrete Employee implementation for Interns.
 */
public class InternEmployee extends Employee {
    private static final double TAX_EXEMPT_STIPEND_LIMIT = 2000.0;
    private static final double STIPEND_SURTAX_RATE = 0.05;

    private double stipend;

    public InternEmployee(int id, String name, String email, String department, String bankAccountNumber, double stipend) {
        super(id, name, email, department, bankAccountNumber);
        if (stipend < 0) {
            throw new IllegalArgumentException("Stipend must be non-negative");
        }
        this.stipend = stipend;
    }

    public double getStipend() {
        return stipend;
    }

    public void setStipend(double stipend) {
        if (stipend < 0) throw new IllegalArgumentException("Stipend cannot be negative");
        this.stipend = stipend;
    }

    @Override
    public double calculateGrossPay() {
        return stipend;
    }

    @Override
    public double calculateTax() {
        if (stipend > TAX_EXEMPT_STIPEND_LIMIT) {
            return (stipend - TAX_EXEMPT_STIPEND_LIMIT) * STIPEND_SURTAX_RATE;
        }
        return 0.0;
    }

    @Override
    public double calculateDeductions() {
        return 0.0;
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
