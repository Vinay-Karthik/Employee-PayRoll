package model;

/**
 * Concrete Employee implementation for Independent Contractors.
 */
public class ContractorEmployee extends Employee {
    private static final double SERVICE_FEE_RATE = 0.02;

    private double hourlyRate;
    private double hoursWorked;

    public ContractorEmployee(int id, String name, String email, String department, String bankAccountNumber,
                              double hourlyRate, double hoursWorked) {
        super(id, name, email, department, bankAccountNumber);
        if (hourlyRate < 0 || hoursWorked < 0) {
            throw new IllegalArgumentException("Hourly rate and hours worked must be non-negative");
        }
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate < 0) throw new IllegalArgumentException("Hourly rate cannot be negative");
        this.hourlyRate = hourlyRate;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        if (hoursWorked < 0) throw new IllegalArgumentException("Hours worked cannot be negative");
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateGrossPay() {
        return hoursWorked * hourlyRate;
    }

    @Override
    public double calculateTax() {
        return 0.0; // Taxes handled independently by contractors
    }

    @Override
    public double calculateDeductions() {
        return calculateGrossPay() * SERVICE_FEE_RATE;
    }

    @Override
    public String getRoleCategory() {
        return "Independent Contractor";
    }

    @Override
    public int getTypeCode() {
        return 3;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("$%.2f/hr x %.1f hrs", hourlyRate, hoursWorked);
    }
}
