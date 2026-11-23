package ui.command;

import service.PayrollService;
import ui.view.ConsoleView;

import java.util.Scanner;

/**
 * Command to delete an employee.
 */
public class DeleteEmployeeCommand implements MenuCommand {
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;

    public DeleteEmployeeCommand(PayrollService payrollService, ConsoleView view, Scanner scanner) {
        this.payrollService = payrollService;
        this.view = view;
        this.scanner = scanner;
    }

    @Override
    public boolean execute() {
        System.out.print("Enter Employee ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean removed = payrollService.removeEmployee(id);
            if (removed) {
                view.displayMessage("Employee with ID " + id + " has been successfully removed.");
            } else {
                view.displayMessage("Employee ID " + id + " was not found!");
            }
        } catch (NumberFormatException e) {
            view.displayMessage("Please enter a valid numeric Employee ID.");
        }
        return true;
    }
}
