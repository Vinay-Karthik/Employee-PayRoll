package ui.command;

import model.entity.Employee;
import service.PayrollService;
import ui.view.ConsoleView;

import java.util.List;
import java.util.Scanner;

/**
 * Helper class to prompt for department selection.
 */
public class DepartmentPrompter {
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;

    public DepartmentPrompter(PayrollService payrollService, ConsoleView view, Scanner scanner) {
        this.payrollService = payrollService;
        this.view = view;
        this.scanner = scanner;
    }

    public String promptDepartment() {
        List<String> departments = payrollService.getAllEmployees().stream()
                .map(Employee::getDepartment)
                .distinct()
                .sorted()
                .toList();

        if (!departments.isEmpty()) {
            view.displayMessage("Existing departments:");
            for (int i = 0; i < departments.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, departments.get(i));
            }
        }

        System.out.print("Select department");
        if (!departments.isEmpty()) {
            System.out.print(" (1-" + departments.size() + ")");
        }
        System.out.print(" or enter a new department name: ");

        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= departments.size()) {
                return departments.get(choice - 1);
            }
            throw new IllegalArgumentException("Invalid department selection: " + choice);
        } catch (NumberFormatException ignored) {
            // Not a number - treat as a new department name
        }

        if (input.isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty");
        }
        return input;
    }
}
