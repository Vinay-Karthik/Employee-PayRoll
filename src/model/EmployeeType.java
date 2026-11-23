package model;

/**
 * Unified employee classifications supported across the payroll system.
 * Aligns employee entities, salary calculation factories, tax/deduction policies,
 * and payroll request builders.
 */
public enum EmployeeType {
    FULL_TIME("Full-Time Salaried"),
    PART_TIME("Part-Time Hourly"),
    CONTRACTOR("Independent Contractor"),
    INTERN("Intern");

    private final String displayName;

    EmployeeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
