package creational.factorymethod;

/**
 * Holds all possible salary components for every employee type.
 * Not all fields are used by every calculator - each calculator
 * reads only the components relevant to its salary structure.
 *
 * Mutable so it can be reused as a nested object inside prototypes
 * and payroll requests; a copy constructor supports deep copying.
 */
public class SalaryComponents {
    private double basic;
    private double hra;
    private double allowances;
    private double fixedContractAmount;
    private double fixedSalary;
    private double commission;
    private double bonus;
    private double benefits;

    public SalaryComponents() {
    }

    /**
     * Copy constructor used for deep copying in the Prototype pattern.
     */
    public SalaryComponents(SalaryComponents other) {
        this.basic = other.basic;
        this.hra = other.hra;
        this.allowances = other.allowances;
        this.fixedContractAmount = other.fixedContractAmount;
        this.fixedSalary = other.fixedSalary;
        this.commission = other.commission;
        this.bonus = other.bonus;
        this.benefits = other.benefits;
    }

    private static void requireNonNegative(String field, double value) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " cannot be negative: " + value);
        }
    }

    public double getBasic() { return basic; }
    public void setBasic(double basic) { requireNonNegative("Basic", basic); this.basic = basic; }

    public double getHra() { return hra; }
    public void setHra(double hra) { requireNonNegative("HRA", hra); this.hra = hra; }

    public double getAllowances() { return allowances; }
    public void setAllowances(double allowances) { requireNonNegative("Allowances", allowances); this.allowances = allowances; }

    public double getFixedContractAmount() { return fixedContractAmount; }
    public void setFixedContractAmount(double fixedContractAmount) { requireNonNegative("Fixed contract amount", fixedContractAmount); this.fixedContractAmount = fixedContractAmount; }

    public double getFixedSalary() { return fixedSalary; }
    public void setFixedSalary(double fixedSalary) { requireNonNegative("Fixed salary", fixedSalary); this.fixedSalary = fixedSalary; }

    public double getCommission() { return commission; }
    public void setCommission(double commission) { requireNonNegative("Commission", commission); this.commission = commission; }

    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { requireNonNegative("Bonus", bonus); this.bonus = bonus; }

    public double getBenefits() { return benefits; }
    public void setBenefits(double benefits) { requireNonNegative("Benefits", benefits); this.benefits = benefits; }

    @Override
    public String toString() {
        return String.format(
                "SalaryComponents{basic=%.2f, hra=%.2f, allowances=%.2f, fixedContract=%.2f, fixedSalary=%.2f, commission=%.2f, bonus=%.2f, benefits=%.2f}",
                basic, hra, allowances, fixedContractAmount, fixedSalary, commission, bonus, benefits);
    }
}
