package ui.command;

import decorator.pipeline.SalaryEnhancementPipeline;
import ui.cli.PayrollCLI;
import ui.command.decorator.DecoratorCommandExecutor;
import ui.view.ConsoleView;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Command to handle dynamic salary enhancement using Decorator Pattern.
 */
public class DynamicSalaryEnhancementCommand implements MenuCommand {
    private final ConsoleView view;
    private final Scanner scanner;

    public DynamicSalaryEnhancementCommand(ConsoleView view, Scanner scanner) {
        this.view = view;
        this.scanner = scanner;
    }

    @Override
    public boolean execute() {
        view.displayMessage("\n=== Dynamic Salary Enhancement (Decorator Pattern) ===");
        try {
            System.out.print("Enter Base Salary: ");
            BigDecimal base = PayrollCLI.parseAmount(scanner.nextLine());
            SalaryEnhancementPipeline pipeline = new SalaryEnhancementPipeline(base);
            String currency = view.getCurrency();

            boolean adding = true;
            while (adding) {
                System.out.println("\nCurrent Configuration: " + pipeline.getCurrentDescription());
                System.out.printf("Current Calculated Salary: %s %.2f%n", currency, pipeline.getCurrentSalary());
                System.out.println("\nSelect Decorator to wrap:");
                System.out.println("1. Add HRA (+20% of base)");
                System.out.println("2. Add Transport Allowance (+" + currency + " 1,600.00)");
                System.out.println("3. Add Performance Bonus (+10% of base)");
                System.out.println("4. Apply Insurance Deduction (-" + currency + " 150.00)");
                System.out.println("5. Custom HRA (specify amount)");
                System.out.println("6. Custom Transport Allowance (specify amount)");
                System.out.println("7. Custom Performance Bonus (specify amount)");
                System.out.println("8. Custom Insurance Deduction (specify amount)");
                System.out.println("9. Complete & View Enhanced Total");
                System.out.print("Enter choice (1-9): ");

                String subChoice = scanner.nextLine().trim();
                DecoratorCommandExecutor.execute(subChoice, pipeline, scanner, view, currency);
                if (subChoice.equals("9")) {
                    adding = false;
                }
            }

            System.out.println("\n==============================================");
            System.out.println("         FINAL ENHANCED SALARY SUMMARY        ");
            System.out.println("==============================================");
            System.out.printf("Base Salary        : %s %.2f%n", currency, pipeline.getBaseSalary());
            System.out.println("Decorator Pipeline : " + pipeline.getCurrentDescription());
            System.out.printf("Total Enhanced Pay : %s %.2f%n", currency, pipeline.getCurrentSalary());
            System.out.println("==============================================");

        } catch (IllegalArgumentException | IllegalStateException e) {
            view.displayMessage("Error: " + e.getMessage());
        }
        return true;
    }
}
