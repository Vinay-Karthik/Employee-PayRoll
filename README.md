# Enterprise Payroll Application

A modular, clean, and maintainable Java payroll system adhering strictly to **SOLID design principles** and **Enterprise Java package naming conventions**.

---

## 🏗️ Architecture & Package Structure

Packages are organized by **business domain and architectural responsibility** rather than pattern names:

```
Employee-PayRoll/
├── src/
│   ├── config/                        # System configuration & environment settings
│   │   └── PayrollConfig.java         # Singleton configuration provider (currency, FY, regime)
│   ├── model/                         # Domain entities, value objects & request models
│   │   ├── Employee.java              # Abstract base domain employee
│   │   ├── FullTimeEmployee.java      # Salaried full-time employee
│   │   ├── PartTimeEmployee.java      # Hourly part-time employee with overtime rules
│   │   ├── ContractorEmployee.java    # Billed contractor with service fee withholding
│   │   ├── InternEmployee.java        # Intern with stipend tax exemption limits
│   │   ├── EmployeeType.java          # Employment structure categories enum
│   │   ├── SalaryComponents.java      # Monetary salary components (with copy constructor)
│   │   ├── PaySlip.java               # Immutable record DTO for calculation results
│   │   ├── PayrollRequest.java        # Builder-constructed immutable payroll run request
│   │   ├── PayrollTemplate.java       # Prototype-clonable reusable configuration
│   │   └── PayrollTemplateCatalog.java# Predefined template catalog
│   ├── calculator/                    # Salary calculation engines (Factory Method Pattern)
│   │   ├── SalaryCalculator.java      # Product interface for gross pay calculation
│   │   ├── SalaryCalculatorFactory.java# GoF Abstract Creator contract
│   │   ├── PermanentSalaryCalculator.java
│   │   ├── PermanentSalaryCalculatorFactory.java
│   │   ├── ContractSalaryCalculator.java
│   │   ├── ContractSalaryCalculatorFactory.java
│   │   ├── SalesSalaryCalculator.java
│   │   ├── SalesSalaryCalculatorFactory.java
│   │   ├── ExecutiveSalaryCalculator.java
│   │   └── ExecutiveSalaryCalculatorFactory.java
│   ├── decorator/                     # Dynamic salary enhancement (Decorator Pattern)
│   │   ├── SalaryCalculator.java      # Component interface
│   │   ├── BaseSalaryCalculator.java  # Concrete component
│   │   ├── SalaryDecorator.java      # Abstract base decorator
│   │   ├── HraDecorator.java          # House Rent Allowance decorator
│   │   ├── TransportAllowanceDecorator.java # Transport allowance decorator
│   │   ├── PerformanceBonusDecorator.java   # Performance bonus decorator
│   │   └── InsuranceDeductionDecorator.java # Insurance deduction decorator
│   ├── tax/                           # Tax regime families (Abstract Factory Pattern)
│   │   ├── TaxRegime.java             # OLD vs NEW regime enum
│   │   ├── TaxCalculator.java         # Tax component interface
│   │   ├── DeductionCalculator.java   # Deduction component interface
│   │   ├── PayrollComponentFactory.java# Abstract Factory for tax/deduction families
│   │   ├── OldTaxRegimeFactory.java   # Concrete factory for Old Regime
│   │   ├── OldTaxCalculator.java
│   │   ├── OldDeductionCalculator.java
│   │   ├── NewTaxRegimeFactory.java   # Concrete factory for New Regime
│   │   ├── NewTaxCalculator.java
│   │   └── NewDeductionCalculator.java
│   ├── integration/                   # External systems integration (Adapter Pattern)
│   │   ├── EmployeeDirectory.java     # Target interface for employee discovery/persistence
│   │   ├── RepositoryEmployeeDirectory.java # Alternate implementation backed by local repository
│   │   ├── ExternalHRAdapter.java     # Adapter converting external records into domain models
│   │   ├── ExternalHRService.java     # Third-party HR API simulation (Adaptee)
│   │   ├── HRRecord.java              # External DTO
│   │   ├── ExternalEmployeeNotFoundException.java
│   │   ├── ExternalServiceException.java
│   │   └── EmployeeDirectoryException.java
│   ├── repository/                    # Persistence abstractions & in-memory stores
│   │   ├── EmployeeRepository.java
│   │   └── InMemoryEmployeeRepository.java
│   ├── service/                       # Core payroll business workflow services
│   │   ├── PayrollService.java
│   │   └── PayrollServiceImpl.java
│   ├── report/                        # Report generation & file export
│   │   ├── ReportExporter.java
│   │   └── FileReportExporter.java
│   ├── ui/                            # Presentation layer & CLI controllers
│   │   ├── ConsoleView.java
│   │   └── PayrollCLI.java
│   └── PayrollSystem.java             # Composition Root: Dependency Injection entry point
├── payroll_summary_report.txt         # Exported report artifact
└── out/                               # Compiled bytecode
```

---

## 🎨 Design Patterns in the Clean Architecture

| Pattern | Clean Package | Key Classes | Responsibility |
| :--- | :--- | :--- | :--- |
| **Decorator** | `decorator` | [`SalaryCalculator`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/decorator/SalaryCalculator.java), [`BaseSalaryCalculator`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/decorator/BaseSalaryCalculator.java), [`HraDecorator`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/decorator/HraDecorator.java), [`TransportAllowanceDecorator`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/decorator/TransportAllowanceDecorator.java), [`PerformanceBonusDecorator`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/decorator/PerformanceBonusDecorator.java), [`InsuranceDeductionDecorator`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/decorator/InsuranceDeductionDecorator.java) | Dynamically attaches allowances and deductions to base salary in arbitrary orders without altering original component. |
| **Factory Method** | `calculator` | [`SalaryCalculatorFactory`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/calculator/SalaryCalculatorFactory.java), `PermanentSalaryCalculatorFactory`, etc. | Defers gross pay calculator instantiation to specialized creator subclasses per employment type. |
| **Abstract Factory** | `tax` | [`PayrollComponentFactory`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/tax/PayrollComponentFactory.java), `OldTaxRegimeFactory`, `NewTaxRegimeFactory` | Produces matching families of tax and deduction calculators for tax regimes. |
| **Adapter** | `integration` | [`ExternalHRAdapter`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/integration/ExternalHRAdapter.java), [`EmployeeDirectory`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/integration/EmployeeDirectory.java) | Converts third-party [`HRRecord`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/integration/HRRecord.java) from [`ExternalHRService`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/integration/ExternalHRService.java) into domain `Employee` models. |
| **Builder** | `model` | [`PayrollRequest.Builder`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/model/PayrollRequest.java) | Validates and constructs immutable, defensive-copied payroll calculation requests. |
| **Prototype** | `model` | [`PayrollTemplate`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/model/PayrollTemplate.java), [`PayrollTemplateCatalog`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/model/PayrollTemplateCatalog.java) | Deep-copies reusable payroll configurations that can be customized per employee. |
| **Singleton** | `config` | [`PayrollConfig`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/config/PayrollConfig.java) | Thread-safe enum singleton providing global default currency, FY, and tax regime. |

---

## 💡 Dynamic Salary Enhancement (Decorator Pattern)

Decorators can wrap any `SalaryCalculator` in any sequence without modifying the original base calculator:

```java
// 1. Base Component
decorator.SalaryCalculator calc = new BaseSalaryCalculator(new BigDecimal("50000.00"));

// 2. Wrap dynamically in arbitrary combinations
calc = new HraDecorator(calc);                             // +20% base ($10,000.00)
calc = new TransportAllowanceDecorator(calc);              // +$1,600.00
calc = new PerformanceBonusDecorator(calc);                // +10% base ($5,000.00)
calc = new InsuranceDeductionDecorator(calc);              // -$150.00

BigDecimal net = calc.calculateSalary();                   // $66,450.00
String details = calc.getDescription();
// "Base Salary ($50000.00) + HRA ($10000.00) + Transport Allowance ($1600.00) + Performance Bonus ($5000.00) - Insurance Deduction ($150.00)"
```

---

## 🚀 Running the Application

```bash
# Compile
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java).FullName

# Run
java -cp out PayrollSystem
```
