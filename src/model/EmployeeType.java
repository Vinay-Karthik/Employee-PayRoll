package model;

/**
 * Unified employee classifications supported across the payroll system.
 * Aligns employee entities, salary calculation factories, tax/deduction policies,
 * and payroll request builders.
 */
public enum EmployeeType {
    FULL_TIME(1, "Full-Time Salaried"),
    PART_TIME(2, "Part-Time Hourly"),
    CONTRACTOR(3, "Independent Contractor"),
    INTERN(4, "Intern");

    private final int typeCode;
    private final String displayName;

    EmployeeType(int typeCode, String displayName) {
        this.typeCode = typeCode;
        this.displayName = displayName;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EmployeeType fromTypeCode(int typeCode) {
        for (EmployeeType type : values()) {
            if (type.typeCode == typeCode) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown employee type code: " + typeCode);
    }
}
