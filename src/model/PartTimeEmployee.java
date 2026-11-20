package model;

/**
 * Concrete Employee implementation for Part-Time Hourly workers.
 */
public class PartTimeEmployee extends Employee {
    private static final double REGULAR_HOURS_THRESHOLD = 40.0;
    private static final double OVERTIME_MULTIPLIER = 1.5;
    private static final double TAX_RATE = 0.12;

    private double hourlyRate;
    private double hoursWorked;

    public PartTimeEmployee(int id, String name, String email, String department, String bankAccountNumber,
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
        if (hoursWorked > REGULAR_HOURS_THRESHOLD) {
            double regularPay = REGULAR_HOURS_THRESHOLD * hourlyRate;
            double overtimePay = (hoursWorked - REGULAR_HOURS_THRESHOLD) * (hourlyRate * OVERTIME_MULTIPLIER);
            return regularPay + overtimePay;
        }
        return hoursWorked * hourlyRate;
    }

    @Override
    public double calculateTax() {
        return calculateGrossPay() * TAX_RATE;
    }

    @Override
    public double calculateDeductions() {
        return 0.0;
    }

    @Override
    public String getRoleCategory() {
        return "Part-Time Hourly";
    }

    @Override
    public int getTypeCode() {
        return 2;
    }

    @Override
    public String getPayDetailsSummary() {
        return String.format("$%.2f/hr x %.1f hrs", hourlyRate, hoursWorked);
    }
}
