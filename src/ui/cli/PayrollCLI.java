package ui.cli;

import decorator.pipeline.DecoratorType;
import decorator.pipeline.SalaryEnhancementPipeline;
import model.entity.*;
import model.value.PaySlip;
import model.request.PayrollRequest;
import model.value.SalaryComponents;
import service.PayrollFacade;
import service.PayrollService;
import tax.regime.TaxRegime;
import ui.command.*;
import ui.command.decorator.DecoratorCommandExecutor;
import ui.view.ConsoleView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Controller for CLI interaction.
 * Coordinates user input with PayrollFacade and ConsoleView.
 * Keeps business rules strictly outside this presentation controller.
 */
public class PayrollCLI {
    private final PayrollFacade payrollFacade;
    private final PayrollService payrollService;
    private final ConsoleView view;
    private final Scanner scanner;
    private final String reportFilePath;
    private final String defaultFinancialYear;
    private final DepartmentPrompter departmentPrompter;
    private final TaxRegimePrompter taxRegimePrompter;
    private final java.util.Map<Integer, MenuCommand> menuCommands;

    public PayrollCLI(PayrollFacade payrollFacade, PayrollService payrollService,
                      ConsoleView view, Scanner scanner, String reportFilePath,
                      String defaultFinancialYear) {
        this.payrollFacade = Objects.requireNonNull(payrollFacade, "PayrollFacade must not be null");
        this.payrollService = Objects.requireNonNull(payrollService, "PayrollService must not be null");
        this.view = Objects.requireNonNull(view, "ConsoleView must not be null");
        this.scanner = Objects.requireNonNull(scanner, "Scanner must not be null");
        this.reportFilePath = Objects.requireNonNull(reportFilePath, "Report file path must not be null");
        this.defaultFinancialYear = (defaultFinancialYear != null && !defaultFinancialYear.isBlank())
                ? defaultFinancialYear : "FY2025-26";
        this.departmentPrompter = new DepartmentPrompter(payrollService, view, scanner);
        this.taxRegimePrompter = new TaxRegimePrompter(view, scanner);
        this.menuCommands = initializeCommands();
    }

    public PayrollCLI(PayrollFacade payrollFacade, PayrollService payrollService,
                      ConsoleView view, Scanner scanner, String reportFilePath) {
        this(payrollFacade, payrollService, view, scanner, reportFilePath, "FY2025-26");
    }

    public void start() {
        view.displayWelcome();
        boolean running = true;

        while (running) {
            view.displayMenu();
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                view.displayMessage("Invalid input! Please enter a number between 1 and 8.");
                continue;
            }

            MenuCommand command = menuCommands.get(choice);
            if (command != null) {
                running = command.execute();
            } else {
                view.displayMessage("Invalid selection. Please choose an option from 1 to 8.");
            }
        }
    }

    private Map<Integer, MenuCommand> initializeCommands() {
        Map<Integer, MenuCommand> commands = new java.util.LinkedHashMap<>();
        commands.put(1, new ListEmployeesCommand(payrollService, view));
        commands.put(2, new AddEmployeeCommand(payrollService, view, scanner, departmentPrompter, taxRegimePrompter));
        commands.put(3, new IndividualPaySlipCommand(payrollService, view, scanner));
        commands.put(4, new FullPayrollCommand(payrollFacade, view, reportFilePath));
        commands.put(5, new DeleteEmployeeCommand(payrollService, view, scanner));
        commands.put(6, new PayrollRequestCommand(payrollService, view, scanner, departmentPrompter, taxRegimePrompter, defaultFinancialYear));
        commands.put(7, new DynamicSalaryEnhancementCommand(view, scanner));
        commands.put(8, new ExitCommand(view));
        return commands;
    }

    public static BigDecimal parseAmount(String input) {
        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: '" + input + "'");
        }
    }
}
