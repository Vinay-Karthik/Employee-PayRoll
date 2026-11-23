package ui.command;

/**
 * Command Pattern interface for menu actions.
 * Encapsulates a request as an object, allowing parameterization
 * and queuing of menu operations.
 */
public interface MenuCommand {
    /**
     * Executes the menu action.
     * @return true if the application should continue running, false to exit
     */
    boolean execute();
}
