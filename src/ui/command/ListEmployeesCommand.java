package ui.command;

import model.entity.Employee;
import service.PayrollService;
import ui.view.ConsoleView;

import java.util.List;

/**
 * Command to list all employees.
 */
public class ListEmployeesCommand implements MenuCommand {
    private final PayrollService payrollService;
    private final ConsoleView view;

    public ListEmployeesCommand(PayrollService payrollService, ConsoleView view) {
        this.payrollService = payrollService;
        this.view = view;
    }

    @Override
    public boolean execute() {
        List<Employee> employees = payrollService.getAllEmployees();
        if (employees.isEmpty()) {
            view.displayMessage("No employees currently registered in the system.");
        } else {
            view.displayEmployees(employees);
        }
        return true;
    }
}
