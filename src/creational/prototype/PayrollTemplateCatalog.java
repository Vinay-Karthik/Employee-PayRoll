package creational.prototype;

import creational.factorymethod.EmployeeType;
import creational.factorymethod.SalaryComponents;

import java.math.BigDecimal;
import java.util.List;

/**
 * Catalog of reusable payroll templates.
 *
 * Integrated into the application: the CLI lets users pick a template,
 * clone it via the Prototype pattern, customize the clone, and turn it
 * into a PayrollRequest for processing.
 */
public final class PayrollTemplateCatalog {

    private PayrollTemplateCatalog() {
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    /**
     * Returns the built-in payroll templates available for cloning.
     */
    public static List<PayrollTemplate> getTemplates() {
        SalaryComponents permanent = new SalaryComponents();
        permanent.setBasic(bd("60000.00"));
        permanent.setHra(bd("24000.00"));
        permanent.setAllowances(bd("6000.00"));

        SalaryComponents contract = new SalaryComponents();
        contract.setFixedContractAmount(bd("45000.00"));

        SalaryComponents sales = new SalaryComponents();
        sales.setFixedSalary(bd("40000.00"));
        sales.setCommission(bd("15000.00"));

        SalaryComponents executive = new SalaryComponents();
        executive.setFixedSalary(bd("120000.00"));
        executive.setBonus(bd("30000.00"));
        executive.setBenefits(bd("12000.00"));

        PayrollTemplate permanentTemplate = new PayrollTemplate("Standard Permanent", EmployeeType.PERMANENT, permanent);
        permanentTemplate.addDeduction("Health Insurance", bd("150.00"));

        PayrollTemplate contractTemplate = new PayrollTemplate("Fixed Contract", EmployeeType.CONTRACT, contract);

        PayrollTemplate salesTemplate = new PayrollTemplate("Sales Package", EmployeeType.SALES, sales);
        salesTemplate.setBonusDetails("Quarterly commission review");

        PayrollTemplate executiveTemplate = new PayrollTemplate("Executive Package", EmployeeType.EXECUTIVE, executive);
        executiveTemplate.addDeduction("Executive Health Plan", bd("500.00"));
        executiveTemplate.setBonusDetails("Annual performance bonus");

        return List.of(permanentTemplate, contractTemplate, salesTemplate, executiveTemplate);
    }
}
