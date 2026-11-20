package model;

/**
 * Concrete Employee implementation for Full-Time Salaried workers.
 */
public class FullTimeEmployee extends Employee {
    private static final double STANDARD_TAX_RATE = 0.22;
    private static final double HIGH_EARNER_THRESHOLD = 7000.0;
    private static final double SURTAX_RATE = 0.05;
    private static final double HEALTH_INSURANCE_DEDUCTION = 150.0;
    private static final double RETIREMENT_FUND_RATE = 0.05;

    private double baseSalary;
    private double bonus;

    public FullTimeEmployee(int id, String name, String email, String department, String bankAccountNumber,
                            double baseSalary, double bonus) {
        super(id, name, email, department, bankAccountNumber);
        if (baseSalary < 0 || bonus < 0) {
            throw new IllegalArgumentException("Base salary and bonus must be non-negative");
        }
        this.baseSalary = baseSalary;
        this.bonus = bonus;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        if (baseSalary < 0) throw new IllegalArgumentException("Base salary cannot be negative");
        this.baseSalary = baseSalary;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        if (bonus < 0) throw new IllegalArgumentException("Bonus cannot be negative");
        this.bonus = bonus;
    }

    @Override
    public double calculateGrossPay() {
        return baseSalary + bonus;
    }

    @Override
    public double calculateTax() {
        double gross = calculateGrossPay();
        double tax = gross * STANDARD_TAX_RATE;
        if (gross > HIGH_EARNER_THRESHOLD) {
            tax += (gross - HIGH_EARNER_THRESHOLD) * SURTAX_RATE;
        }
        return tax;
    }

    @Override
    public double calculateDeductions() {
        return HEALTH_INSURANCE_DEDUCTION + (calculateGrossPay() * RETIREMENT_FUND_RATE);
    }

    @Override
    public String getRoleCategory() {
        return "Full-Time Salaried";
    }

    @Override
    public int getTypeCode() {
        return 1;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("$%.2f (Bonus: $%.2f)", baseSalary, bonus);
    }
}
