package service;

import creational.abstractfactory.DeductionCalculator;
import creational.abstractfactory.PayrollComponentFactory;
import creational.abstractfactory.TaxCalculator;
import creational.builder.PayrollRequest;
import creational.factorymethod.EmployeeType;
import creational.factorymethod.SalaryCalculator;
import creational.factorymethod.SalaryCalculatorFactory;
import creational.singleton.PayrollConfig;
import model.Employee;
import model.PaySlip;
import report.ReportExporter;
import repository.EmployeeRepository;
import structural.adapter.EmployeeDirectory;
import structural.adapter.RepositoryEmployeeDirectory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Concrete implementation of PayrollService.
 * Coordinates employee directory, domain calculations, and report generation.
 *
 * Design Pattern Integration:
 * - Structural Adapter: EmployeeDirectory interface decouples payroll processing from
 *   the underlying employee data source (e.g. ExternalHRAdapter vs RepositoryEmployeeDirectory).
 * - Read/Write Consistency: All employee lifecycle operations (retrieval, registration, removal)
 *   are handled consistently through the EmployeeDirectory, maintaining data synchronization.
 * - Singleton: PayrollConfig provides default currency & fallback financial year.
 * - GoF Factory Method: SalaryCalculatorFactory defines the abstract creator whose
 *   subclasses instantiate specific SalaryCalculator products.
 * - Abstract Factory: PayrollComponentFactory provides regime-compatible
 *   tax and deduction calculators.
 * - Builder & Prototype: Request-specific parameters are honored and preserved on PaySlips.
 */
public class PayrollServiceImpl implements PayrollService {
    private final EmployeeDirectory employeeDirectory;
    private final EmployeeRepository repository;
    private final ReportExporter reportExporter;
    private final Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver;

    /**
     * Primary constructor injecting EmployeeDirectory via constructor injection (Adapter Pattern).
     */
    public PayrollServiceImpl(EmployeeDirectory employeeDirectory,
                              EmployeeRepository repository,
                              ReportExporter reportExporter,
                              Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver) {
        this.employeeDirectory = Objects.requireNonNull(employeeDirectory, "EmployeeDirectory must not be null");
        this.repository = Objects.requireNonNull(repository, "Repository must not be null");
        this.reportExporter = Objects.requireNonNull(reportExporter, "Report exporter must not be null");
        this.salaryCalculatorFactoryResolver = Objects.requireNonNull(salaryCalculatorFactoryResolver, "Factory resolver must not be null");
    }

    public PayrollServiceImpl(EmployeeDirectory employeeDirectory,
                              EmployeeRepository repository,
                              ReportExporter reportExporter) {
        this(employeeDirectory, repository, reportExporter, SalaryCalculatorFactory::forType);
    }

    /**
     * Overloaded constructor for backward compatibility.
     * Uses RepositoryEmployeeDirectory as default adapter for EmployeeRepository.
     */
    public PayrollServiceImpl(EmployeeRepository repository, ReportExporter reportExporter) {
        this(new RepositoryEmployeeDirectory(repository), repository, reportExporter, SalaryCalculatorFactory::forType);
    }

    public PayrollServiceImpl(EmployeeRepository repository, ReportExporter reportExporter,
                              Function<EmployeeType, SalaryCalculatorFactory> salaryCalculatorFactoryResolver) {
        this(new RepositoryEmployeeDirectory(repository), repository, reportExporter, salaryCalculatorFactoryResolver);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeDirectory.findAllEmployees();
    }

    @Override
    public Optional<Employee> getEmployeeById(int id) {
        return employeeDirectory.findEmployeeById(id);
    }

    /**
     * Registers an employee consistently into the employee directory,
     * maintaining synchronization with the repository.
     */
    @Override
    public void registerEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        employeeDirectory.saveEmployee(employee);
        if (repository != null) {
            repository.save(employee);
        }
    }

    /**
     * Removes an employee consistently from the employee directory and repository.
     */
    @Override
    public boolean removeEmployee(int id) {
        boolean removedFromDirectory = employeeDirectory.deleteEmployee(id);
        boolean removedFromRepo = (repository != null) && repository.deleteById(id);
        return removedFromDirectory || removedFromRepo;
    }

    @Override
    public Optional<PaySlip> processEmployeePaySlip(int id) {
        return employeeDirectory.findEmployeeById(id).map(Employee::generatePaySlip);
    }

    @Override
    public List<PaySlip> processAllPaySlips() {
        return employeeDirectory.findAllEmployees().stream()
                .map(Employee::generatePaySlip)
                .toList();
    }

    /**
     * Processes a PayrollRequest end-to-end:
     * 1. Uses the request's specific financial year (falling back to PayrollConfig default).
     * 2. GoF Factory Method -> concrete creator subclass instantiates the appropriate
     *    SalaryCalculator to compute gross pay.
     * 3. Abstract Factory -> regime-compatible tax & deduction calculators.
     * 4. Optional deductions declared on the request are added on top.
     * 5. Carries descriptive bonus details into the resulting PaySlip.
     */
    @Override
    public PaySlip processPayrollRequest(PayrollRequest request) {
        Objects.requireNonNull(request, "Payroll request must not be null");

        // Use the request's financial year, using singleton config as fallback default
        String effectiveFinancialYear = (request.getFinancialYear() != null && !request.getFinancialYear().isBlank())
                ? request.getFinancialYear()
                : PayrollConfig.getInstance().getFinancialYear();

        // GoF Factory Method: resolve concrete creator subclass and invoke factory method
        SalaryCalculatorFactory creator = salaryCalculatorFactoryResolver.apply(request.getEmployeeType());
        SalaryCalculator salaryCalculator = creator.createCalculator();
        BigDecimal gross = salaryCalculator.calculateGross(request.getSalaryComponents());

        PayrollComponentFactory componentFactory = PayrollComponentFactory.forRegime(request.getTaxRegime());
        TaxCalculator taxCalculator = componentFactory.createTaxCalculator();
        DeductionCalculator deductionCalculator = componentFactory.createDeductionCalculator();

        BigDecimal tax = taxCalculator.calculateTax(gross);
        BigDecimal deductions = deductionCalculator.calculateDeductions(gross)
                .add(request.totalOptionalDeductions());
        BigDecimal net = gross.subtract(tax).subtract(deductions);

        return new PaySlip(
                request.getEmployeeId(),
                request.getEmployeeName(),
                request.getDepartment(),
                request.getEmployeeType().name(),
                request.getBankAccountNumber(),
                gross, tax, deductions, net,
                effectiveFinancialYear,
                request.getBonusDetails()
        );
    }

    @Override
    public String generatePayrollSummaryReport(List<PaySlip> paySlips) {
        PayrollConfig config = PayrollConfig.getInstance();
        String currency = config.getCurrency();

        // Derive financial year from the processed slips, falling back to singleton config
        String reportFinancialYear = paySlips.stream()
                .map(PaySlip::financialYear)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse(config.getFinancialYear());

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
    public void exportSummaryReport(String reportContent, String destination) throws IOException {
        reportExporter.export(reportContent, destination);
    }
}
