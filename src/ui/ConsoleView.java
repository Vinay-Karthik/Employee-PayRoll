package ui;

import config.PayrollConfig;
import model.Employee;
import model.PaySlip;

import java.util.List;

/**
 * Single Responsibility: Dedicated to console rendering and formatting.
 * Does not handle domain logic or direct user input parsing.
 */
public class ConsoleView {

    private final String currency = PayrollConfig.getInstance().getCurrency();

    public String getCurrency() {
        return currency;
    }

    public void displayWelcome() {
        System.out.println("==================================================");
        System.out.println("          SOLID PAYROLL APPLICATION               ");
        System.out.println("==================================================");
    }

    public void displayMenu() {
        System.out.println("\nSelect an option:");
        System.out.println("1. View all employees");
        System.out.println("2. Add new employee");
        System.out.println("3. Calculate & print payslip for an employee");
        System.out.println("4. Process & export full payroll report");
        System.out.println("5. Delete an employee");
        System.out.println("6. Quick payroll via template & request (Builder/Prototype)");
        System.out.println("7. Dynamic salary enhancement (Decorator Pattern)");
        System.out.println("8. Exit");
        System.out.print("Enter choice (1-8): ");
    }

    public void displayEmployees(List<Employee> employees) {
        System.out.println("\n---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-16s | %-14s | %-22s | %-24s\n",
                "ID", "Name", "Department", "Role Category", "Pay Basis / Details");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Employee emp : employees) {
            System.out.printf("%-5d | %-16s | %-14s | %-22s | %-24s\n",
                    emp.getId(), emp.getName(), emp.getDepartment(),
                    emp.getRoleCategory(), emp.getPayDetailsSummary());
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }

    public void displayPaySlip(PaySlip slip) {
        System.out.println("\n==============================================");
        System.out.println("                 PAY SLIP                     ");
        System.out.println("==============================================");
        System.out.println("Employee ID   : " + slip.employeeId());
        System.out.println("Name          : " + slip.employeeName());
        System.out.println("Department    : " + slip.department());
        System.out.println("Role Category : " + slip.roleCategory());
        System.out.println("Financial Year: " + slip.financialYear());
        System.out.println("Bank Account  : " + slip.bankAccountNumber());
        if (slip.bonusDetails() != null && !slip.bonusDetails().isBlank()) {
            System.out.println("Bonus Details : " + slip.bonusDetails());
        }
        System.out.println("----------------------------------------------");
        System.out.printf("Gross Earnings : %s %.2f\n", currency, slip.grossPay());
        System.out.printf("Tax Withholding: %s %.2f\n", currency, slip.taxWithholding());
        System.out.printf("Deductions     : %s %.2f\n", currency, slip.deductions());
        System.out.println("----------------------------------------------");
        System.out.printf("NET PAY        : %s %.2f\n", currency, slip.netPay());
        System.out.println("==============================================\n");
    }

    public void displayReport(String report) {
        System.out.println(report);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayError(String error) {
        System.err.println("Error: " + error);
    }
}
