package ui.command;

import model.value.PaySlip;
import service.PayrollService;
import ui.view.ConsoleView;

import java.util.Optional;
import java.util.Scanner;

/**
 * Command to generate an individual payslip.
 */
public class IndividualPaySlipCommand implements MenuCommand {
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;

    public IndividualPaySlipCommand(PayrollService payrollService, ConsoleView view, Scanner scanner) {
        this.payrollService = payrollService;
        this.view = view;
        this.scanner = scanner;
    }

    @Override
    public boolean execute() {
        System.out.print("Enter Employee ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Optional<PaySlip> slipOpt = payrollService.processEmployeePaySlip(id);
            if (slipOpt.isPresent()) {
                view.displayPaySlip(slipOpt.get());
            } else {
                view.displayMessage("Employee ID " + id + " not found!");
            }
        } catch (NumberFormatException e) {
            view.displayMessage("Please enter a valid numeric Employee ID.");
        }
        return true;
    }
}
