package ui.command;

import model.factory.*;

import java.util.List;

/**
 * Registry for employee factories, providing factory lookup and options display.
 */
public class EmployeeFactoryRegistry {
    private static final List<EmployeeFactory> EMPLOYEE_FACTORIES = List.of(
            new FullTimeEmployeeFactory(),
            new PartTimeEmployeeFactory(),
            new ContractorEmployeeFactory(),
            new InternEmployeeFactory());

    public static String options() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < EMPLOYEE_FACTORIES.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(i + 1).append("=").append(EMPLOYEE_FACTORIES.get(i).type().getDisplayName());
        }
        return sb.toString();
    }

    public static EmployeeFactory factoryFor(int choice) {
        if (choice < 1 || choice > EMPLOYEE_FACTORIES.size()) {
            throw new IllegalArgumentException("Unknown employee role type: " + choice);
        }
        return EMPLOYEE_FACTORIES.get(choice - 1);
    }
}
