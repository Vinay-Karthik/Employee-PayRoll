package ui.command;

import tax.regime.TaxRegime;
import ui.view.ConsoleView;

import java.util.Scanner;

/**
 * Helper class to prompt for tax regime selection.
 */
public class TaxRegimePrompter {
    private final ConsoleView view;
    private final Scanner scanner;

    public TaxRegimePrompter(ConsoleView view, Scanner scanner) {
        this.view = view;
        this.scanner = scanner;
    }

    public TaxRegime promptTaxRegime() {
        while (true) {
            System.out.print("Enter Tax Regime (1=OLD, 2=NEW) [NEW]: ");
            String regimeInput = scanner.nextLine().trim();
            if (regimeInput.isEmpty() || regimeInput.equals("2")) {
                return TaxRegime.NEW;
            } else if (regimeInput.equals("1")) {
                return TaxRegime.OLD;
            } else {
                view.displayMessage("Invalid input! Please enter 1 for OLD or 2 for NEW.");
            }
        }
    }
}
