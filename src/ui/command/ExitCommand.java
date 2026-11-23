package ui.command;

import ui.view.ConsoleView;

/**
 * Command to exit the application.
 */
public class ExitCommand implements MenuCommand {
    private final ConsoleView view;

    public ExitCommand(ConsoleView view) {
        this.view = view;
    }

    @Override
    public boolean execute() {
        view.displayMessage("Exiting Payroll Application. Goodbye!");
        return false;
    }
}
