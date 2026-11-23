package service;

import calculator.SalaryCalculator;
import calculator.SalaryCalculatorFactory;
import decorator.DecoratorType;
import decorator.SalaryEnhancementPipeline;
import model.Employee;
import model.EmployeeType;
import model.PaySlip;
import model.PayrollRequest;
import repository.EmployeeRepository;
import tax.DeductionPolicy;
import tax.TaxPolicy;
import tax.TaxRegime;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Concrete implementation of PayrollService.
 * Coordinates employee repository, domain calculations, and summary report formatting.
 * Report exporting is delegated exclusively to PayrollFacade.
 */
public class PayrollServiceImpl implements PayrollService {
    private final EmployeeRepository employeeRepository;
    private final Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver;
    private final String currency;
    private final String financialYear;

    /**
     * Primary constructor injecting dependencies via constructor injection.
     * Configuration parameters (currency, financialYear) are injected directly.
     */
    public PayrollServiceImpl(EmployeeRepository employeeRepository,
                              Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver,
                              String currency,
                              String financialYear) {
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository must not be null");
        this.salaryCalculatorFactoryResolver = Objects.requireNonNull(salaryCalculatorFactoryResolver, "Factory resolver must not be null");
        this.currency = (currency != null && !currency.isBlank()) ? currency : "USD";
        this.financialYear = (financialYear != null && !financialYear.isBlank()) ? financialYear : "FY2025-26";
    }

    public PayrollServiceImpl(EmployeeRepository employeeRepository,
                              Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver) {
        this(employeeRepository, salaryCalculatorFactoryResolver, "USD", "FY2025-26");
    }

    public PayrollServiceImpl(EmployeeRepository employeeRepository) {
        this(employeeRepository, SalaryCalculatorFactory::forType, "USD", "FY2025-26");
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(int id) {
        return employeeRepository.findById(id);
    }

    @Override
    public void registerEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        employeeRepository.save(employee);
    }

    @Override
    public boolean removeEmployee(int id) {
        return employeeRepository.deleteById(id);
    }

    @Override
    public Optional<PaySlip> processEmployeePaySlip(int id) {
        return employeeRepository.findById(id).map(Employee::generatePaySlip);
    }

    @Override
    public List<PaySlip> processAllPaySlips() {
        return employeeRepository.findAll().stream()
                .map(Employee::generatePaySlip)
                .toList();
    }

    @Override
    public PaySlip processPayrollRequest(PayrollRequest request) {
        Objects.requireNonNull(request, "Payroll request must not be null");

        String effectiveFinancialYear = (request.getFinancialYear() != null && !request.getFinancialYear().isBlank())
                ? request.getFinancialYear()
                : this.financialYear;

        SalaryCalculatorFactory creator = salaryCalculatorFactoryResolver.apply(request.getEmployeeType());
        SalaryCalculator salaryCalculator = creator.createCalculator();
        BigDecimal gross = salaryCalculator.calculateGross(request.getSalaryComponents());

        TaxPolicy taxPolicy = TaxPolicy.forEmployeeType(request.getEmployeeType());
        DeductionPolicy deductionPolicy = DeductionPolicy.forEmployeeType(request.getEmployeeType());

        // Respect the selected tax regime (OLD or NEW) from request
        BigDecimal tax = taxPolicy.calculateTax(gross, request.getTaxRegime());
        BigDecimal deductions = deductionPolicy.calculateDeductions(gross)
                .add(request.totalOptionalDeductions());
        BigDecimal net = gross.subtract(tax).subtract(deductions);

        // Issue 4: Validate net pay is not negative
        if (net.signum() < 0) {
            throw new IllegalStateException(
                    "Calculated net pay is negative (" + net + "). " +
                    "Gross: " + gross + ", Tax: " + tax + ", Deductions: " + deductions +
                    ". Deductions exceed payable salary."
            );
        }

        return new PaySlip(
                request.getEmployeeId(),
                request.getEmployeeName(),
                request.getDepartment(),
                request.getEmployeeType().getDisplayName(),
                request.getBankAccountNumber(),
                gross, tax, deductions, net,
                effectiveFinancialYear,
                request.getBonusDetails()
        );
    }

    @Override
    public String generatePayrollSummaryReport(List<PaySlip> paySlips) {
        String reportFinancialYear = paySlips.stream()
                .map(PaySlip::financialYear)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse(this.financialYear);

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================\n");
        sb.append(String.format("               PAYROLL SUMMARY REPORT - %s (%s)               \n",
                reportFinancialYear, currency));
        sb.append("========================================================================================================\n");
        sb.append(String.format("%-5s | %-16s | %-18s | %-10s | %-12s | %-12s | %-12s | %-12s\n",
                "ID", "Name", "Role Category", "FY", "Gross", "Tax", "Deduction", "Net Pay"));
        sb.append("--------------------------------------------------------------------------------------------------------\n");

        for (PaySlip slip : paySlips) {
            totalGross = totalGross.add(slip.grossPay());
            totalTax = totalTax.add(slip.taxWithholding());
            totalDeductions = totalDeductions.add(slip.deductions());
            totalNet = totalNet.add(slip.netPay());

            sb.append(String.format("%-5d | %-16s | %-18s | %-10s | %s %-9.2f | %s %-9.2f | %s %-9.2f | %s %-9.2f\n",
                    slip.employeeId(), slip.employeeName(), slip.roleCategory(), slip.financialYear(),
                    currency, slip.grossPay(), currency, slip.taxWithholding(),
                    currency, slip.deductions(), currency, slip.netPay()));
        }

        sb.append("========================================================================================================\n");
        sb.append(String.format("TOTALS: Gross: %s %.2f | Tax: %s %.2f | Deductions: %s %.2f | Total Disbursed: %s %.2f\n",
                currency, totalGross, currency, totalTax, currency, totalDeductions, currency, totalNet));
        sb.append("========================================================================================================\n");

        return sb.toString();
    }

    @Override
    public SalaryEnhancementPipeline createSalaryEnhancementPipeline(BigDecimal baseSalary) {
        return new SalaryEnhancementPipeline(baseSalary);
    }

    /**
     * Generates a PaySlip for an employee using the SalaryEnhancementPipeline.
     * Integrates the decorator-based salary enhancement flow with standard
     * employee payslip generation, allowing dynamic application of HRA, allowances,
     * bonuses, and deductions on top of the employee's base salary.
     *
     * @param employeeId the ID of the employee
     * @param decoratorTypes the decorator types to apply (e.g., HRA, TRANSPORT_ALLOWANCE)
     * @return Optional containing the enhanced PaySlip, or empty if employee not found
     * @throws NullPointerException if decoratorTypes array or any of its elements is null
     */
    @Override
    public Optional<PaySlip> generateEnhancedPaySlip(int employeeId, DecoratorType... decoratorTypes) {
        Objects.requireNonNull(decoratorTypes, "decoratorTypes array must not be null");
        for (int i = 0; i < decoratorTypes.length; i++) {
            Objects.requireNonNull(decoratorTypes[i], "decoratorTypes[" + i + "] must not be null");
        }

        return employeeRepository.findById(employeeId).map(employee -> {
            SalaryEnhancementPipeline pipeline = new SalaryEnhancementPipeline(extractBaseSalary(employee));
            for (DecoratorType type : decoratorTypes) {
                pipeline.applyDecorator(type);
            }
            return createPaySlipFromPipeline(employee, pipeline);
        });
    }

    /**
     * Generates a PaySlip for an employee using the SalaryEnhancementPipeline
     * with custom amounts for specific decorators.
     *
     * @param employeeId the ID of the employee
     * @param decoratorCustomAmounts map of decorator types to custom amounts
     * @return Optional containing the enhanced PaySlip, or empty if employee not found
     * @throws NullPointerException if decoratorCustomAmounts map or any key/value is null
     */
    @Override
    public Optional<PaySlip> generateEnhancedPaySlip(int employeeId, Map<DecoratorType, BigDecimal> decoratorCustomAmounts) {
        Objects.requireNonNull(decoratorCustomAmounts, "decoratorCustomAmounts map must not be null");
        for (Map.Entry<DecoratorType, BigDecimal> entry : decoratorCustomAmounts.entrySet()) {
            Objects.requireNonNull(entry.getKey(), "DecoratorType key in map must not be null");
            Objects.requireNonNull(entry.getValue(), "Custom amount value for " + entry.getKey() + " must not be null");
        }

        return employeeRepository.findById(employeeId).map(employee -> {
            SalaryEnhancementPipeline pipeline = new SalaryEnhancementPipeline(extractBaseSalary(employee));
            for (Map.Entry<DecoratorType, BigDecimal> entry : decoratorCustomAmounts.entrySet()) {
                pipeline.applyDecorator(entry.getKey(), entry.getValue());
            }
            return createPaySlipFromPipeline(employee, pipeline);
        });
    }

    /**
     * Creates an enhanced PaySlip from an employee and evaluated SalaryEnhancementPipeline.
     */
    private PaySlip createPaySlipFromPipeline(Employee employee, SalaryEnhancementPipeline pipeline) {
        BigDecimal gross = pipeline.getCurrentSalary();
        BigDecimal tax = calculateTaxForEmployee(employee, gross);
        BigDecimal deductions = calculateDeductionsForEmployee(employee, gross);
        BigDecimal net = gross.subtract(tax).subtract(deductions);

        if (net.signum() < 0) {
            throw new IllegalStateException(
                    "Calculated net pay is negative (" + net + "). " +
                    "Gross: " + gross + ", Tax: " + tax + ", Deductions: " + deductions
            );
        }

        return new PaySlip(
                employee.getId(),
                employee.getName(),
                employee.getDepartment(),
                employee.getRoleCategory(),
                employee.getBankAccountNumber(),
                gross, tax, deductions, net,
                this.financialYear,
                pipeline.getCurrentDescription()
        );
    }

    /**
     * Extracts the base salary from an employee for the enhancement pipeline.
     * Uses the employee's full gross pay as the starting point for the decorator pipeline.
     *
     * @param employee the employee to extract base salary from
     * @return the employee's standard gross pay
     */
    private BigDecimal extractBaseSalary(Employee employee) {
        return employee.calculateGrossPay();
    }

    /**
     * Calculates tax for an employee based on their type and the enhanced gross salary.
     * Delegates to the shared {@link TaxPolicy} for the employee's type.
     */
    private BigDecimal calculateTaxForEmployee(Employee employee, BigDecimal enhancedGross) {
        return TaxPolicy.forEmployee(employee).calculateTax(enhancedGross, employee.getTaxRegime());
    }

    /**
     * Calculates deductions for an employee based on their type and the enhanced gross salary.
     * Delegates to the shared {@link DeductionPolicy} for the employee's type.
     */
    private BigDecimal calculateDeductionsForEmployee(Employee employee, BigDecimal enhancedGross) {
        return DeductionPolicy.forEmployee(employee).calculateDeductions(enhancedGross);
    }
}
