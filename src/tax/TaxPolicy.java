package tax;

import model.Employee;
import model.EmployeeType;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Strategy interface for tax calculation policies.
 * Encapsulates tax rules for each employee category so they can be reused
 * by both standard payslip calculation and enhanced payslip calculation.
 * <p>
 * Employee categories are identified by type codes:
 * 1 = Full-Time, 2 = Part-Time, 3 = Contractor, 4 = Intern
 */
public interface TaxPolicy {

    /**
     * Calculates tax for the given gross amount.
     *
     * @param gross the gross salary amount to calculate tax on
     * @return calculated tax amount
     */
    BigDecimal calculateTax(BigDecimal gross);

    /**
     * Returns the employee type code this policy applies to.
     */
    int getTypeCode();

    /**
     * Returns the tax policy for the given employee type code.
     *
     * @param typeCode the employee type code (1=Full-Time, 2=Part-Time, 3=Contractor, 4=Intern)
     * @return the corresponding tax policy
     * @throws IllegalArgumentException if no policy exists for the type code
     */
    static TaxPolicy forTypeCode(int typeCode) {
        return switch (typeCode) {
            case 1 -> FullTimeTaxPolicy.INSTANCE;
            case 2 -> PartTimeTaxPolicy.INSTANCE;
            case 3 -> ContractorTaxPolicy.INSTANCE;
            case 4 -> InternTaxPolicy.INSTANCE;
            default -> throw new IllegalArgumentException("Unknown employee type code: " + typeCode);
        };
    }

    /**
     * Returns the tax policy for the given employee.
     *
     * @param employee the employee
     * @return the corresponding tax policy
     */
    static TaxPolicy forEmployee(Employee employee) {
        return forTypeCode(employee.getTypeCode());
    }

    /**
     * Returns the tax policy for the given employee type category.
     *
     * @param employeeType the employee type category
     * @return the corresponding tax policy
     */
    static TaxPolicy forEmployeeType(EmployeeType employeeType) {
        Objects.requireNonNull(employeeType, "Employee type must not be null");
        return switch (employeeType) {
            case PERMANENT, EXECUTIVE -> FullTimeTaxPolicy.INSTANCE;
            case CONTRACT -> ContractorTaxPolicy.INSTANCE;
            case SALES -> PartTimeTaxPolicy.INSTANCE;
        };
    }
}
