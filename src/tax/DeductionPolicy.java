package tax;

import model.Employee;
import model.EmployeeType;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Strategy interface for deduction calculation policies.
 * Encapsulates deduction rules for each employee category so they can be reused
 * across standard, enhanced, and request payroll flows.
 */
public interface DeductionPolicy {

    /**
     * Calculates deductions for the given gross amount.
     *
     * @param gross the gross salary amount to calculate deductions on
     * @return calculated deductions amount
     */
    BigDecimal calculateDeductions(BigDecimal gross);

    /**
     * Returns the employee type code this policy applies to.
     */
    int getTypeCode();

    /**
     * Returns the deduction policy for the given employee type code.
     *
     * @param typeCode the employee type code (1=Full-Time, 2=Part-Time, 3=Contractor, 4=Intern)
     * @return the corresponding deduction policy
     * @throws IllegalArgumentException if no policy exists for the type code
     */
    static DeductionPolicy forTypeCode(int typeCode) {
        return switch (typeCode) {
            case 1 -> FullTimeDeductionPolicy.INSTANCE;
            case 2 -> PartTimeDeductionPolicy.INSTANCE;
            case 3 -> ContractorDeductionPolicy.INSTANCE;
            case 4 -> InternDeductionPolicy.INSTANCE;
            default -> throw new IllegalArgumentException("Unknown employee type code: " + typeCode);
        };
    }

    /**
     * Returns the deduction policy for the given employee.
     *
     * @param employee the employee
     * @return the corresponding deduction policy
     */
    static DeductionPolicy forEmployee(Employee employee) {
        return forTypeCode(employee.getTypeCode());
    }

    /**
     * Returns the deduction policy for the given unified employee type category.
     *
     * @param employeeType the employee type category
     * @return the corresponding deduction policy
     */
    static DeductionPolicy forEmployeeType(EmployeeType employeeType) {
        Objects.requireNonNull(employeeType, "Employee type must not be null");
        return switch (employeeType) {
            case FULL_TIME -> FullTimeDeductionPolicy.INSTANCE;
            case PART_TIME -> PartTimeDeductionPolicy.INSTANCE;
            case CONTRACTOR -> ContractorDeductionPolicy.INSTANCE;
            case INTERN -> InternDeductionPolicy.INSTANCE;
        };
    }
}
