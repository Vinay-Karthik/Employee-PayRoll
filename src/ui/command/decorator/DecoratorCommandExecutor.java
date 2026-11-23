package ui.command.decorator;

import decorator.pipeline.DecoratorType;
import decorator.pipeline.SalaryEnhancementPipeline;
import ui.cli.PayrollCLI;
import ui.view.ConsoleView;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Executes decorator commands for the salary enhancement flow.
 * Encapsulates the decorator selection logic.
 */
public class DecoratorCommandExecutor {

    public static void execute(String choice, SalaryEnhancementPipeline pipeline, Scanner scanner,
                               ConsoleView view, String currency) {
        try {
            switch (choice) {
                case "1" -> pipeline.applyDecorator(DecoratorType.HRA);
                case "2" -> pipeline.applyDecorator(DecoratorType.TRANSPORT_ALLOWANCE);
                case "3" -> pipeline.applyDecorator(DecoratorType.PERFORMANCE_BONUS);
                case "4" -> pipeline.applyDecorator(DecoratorType.INSURANCE_DEDUCTION);
                case "5" -> {
                    System.out.print("Enter custom HRA amount: ");
                    pipeline.applyDecorator(DecoratorType.HRA, PayrollCLI.parseAmount(scanner.nextLine()));
                }
                case "6" -> {
                    System.out.print("Enter custom Transport Allowance amount: ");
                    pipeline.applyDecorator(DecoratorType.TRANSPORT_ALLOWANCE, PayrollCLI.parseAmount(scanner.nextLine()));
                }
                case "7" -> {
                    System.out.print("Enter custom Performance Bonus amount: ");
                    pipeline.applyDecorator(DecoratorType.PERFORMANCE_BONUS, PayrollCLI.parseAmount(scanner.nextLine()));
                }
                case "8" -> {
                    System.out.print("Enter custom Insurance Deduction amount: ");
                    pipeline.applyDecorator(DecoratorType.INSURANCE_DEDUCTION, PayrollCLI.parseAmount(scanner.nextLine()));
                }
                case "9" -> { /* Complete - handled by caller */ }
                default -> System.out.println("Invalid selection. Please choose 1-9.");
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            view.displayMessage("Error: " + ex.getMessage());
        }
    }
}
