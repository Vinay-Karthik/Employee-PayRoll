# Payroll Application (Refactored with SOLID Principles)

A modular, clean, and maintainable Java payroll system refactored from an initial legacy monolithic codebase by strictly adhering to the **SOLID** design principles.

---

## 🏗️ Architecture & Package Structure

```
Employee-PayRoll/
├── src/
│   ├── model/
│   │   ├── Employee.java              # Abstract base domain model (Template Method for PaySlip)
│   │   ├── FullTimeEmployee.java      # Concrete employee: base salary + bonus + high-earner surtax
│   │   ├── PartTimeEmployee.java      # Concrete employee: hourly rate + overtime policy
│   │   ├── ContractorEmployee.java    # Concrete employee: flat billable rate + service withholding
│   │   ├── InternEmployee.java        # Concrete employee: fixed stipend + tax exempt threshold
│   │   └── PaySlip.java               # Immutable Value Object / DTO for calculation results
│   ├── repository/
│   │   ├── EmployeeRepository.java    # Interface for persistence abstraction (ISP / DIP)
│   │   └── CsvEmployeeRepository.java # Concrete CSV storage with try-with-resources
│   ├── service/
│   │   ├── PayrollService.java        # Interface for business operations
│   │   └── PayrollServiceImpl.java    # Coordinates domain models, persistence, and reporting
│   ├── report/
│   │   ├── ReportExporter.java        # Interface for output generation
│   │   └── FileReportExporter.java    # Concrete filesystem exporter
│   ├── ui/
│   │   ├── ConsoleView.java           # Presentation layer (table rendering, payslip formatting)
│   │   └── PayrollCLI.java            # Controller handling user interactions and input parsing
│   └── PayrollSystem.java             # Composition Root: wires dependencies together (DI)
├── employees.csv                      # Persistent storage
├── payroll_summary_report.txt         # Exported report artifact
└── out/                               # Compiled bytecode
```

---

## 🔍 Problems Identified in the Initial Codebase

| Problem | Symptoms in Legacy Code | Affected Principle |
| :--- | :--- | :--- |
| **God Class** | `PayrollSystem` handled CSV persistence, salary rules, tax formulas, CLI menu, and payslip formatting. | **S** (Single Responsibility) |
| **Shotgun Surgery** | Adding a new employee role required altering 5 separate switch statements across multiple methods. | **O** (Open-Closed) |
| **Primitive Obsession** | Used raw integer codes (`1, 2, 3, 4`) for employee roles instead of polymorphic behavior. | **O** / **L** |
| **DRY Violations** | Gross pay, tax calculations, and benefits were copy-pasted in 3 different methods. | Maintainability |
| **Tight Coupling** | High-level logic directly called `new FileWriter("employees.csv")` and `System.out.println`. | **D** (Dependency Inversion) |
| **No Abstraction** | No interfaces existed; unit testing was blocked by static global collections. | **I** / **D** |

---

## 🛡️ How SOLID Principles Were Applied

### 1. Single Responsibility Principle (SRP)
- **Model**: [`Employee`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/model/Employee.java) and its subclasses only manage employee state and calculation rules.
- **Repository**: [`CsvEmployeeRepository`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/repository/CsvEmployeeRepository.java) only manages reading/writing records from CSV.
- **Service**: [`PayrollServiceImpl`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/service/PayrollServiceImpl.java) only orchestrates domain operations and aggregates payroll summaries.
- **View**: [`ConsoleView`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/ui/ConsoleView.java) only handles formatting and rendering output.
- **Controller**: [`PayrollCLI`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/ui/PayrollCLI.java) only handles user input validation and menu flow.
- **Root**: [`PayrollSystem`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/PayrollSystem.java) only acts as the composition root for dependency injection.

### 2. Open-Closed Principle (OCP)
- Replaced all switch statements with **polymorphism**.
- To add a new role (e.g., `CommissionEmployee`), create a new class extending `Employee` and implement its specific `calculateGrossPay()`, `calculateTax()`, and `calculateDeductions()`.
- Existing classes (`PayrollServiceImpl`, `CsvEmployeeRepository`, `PaySlip`) remain closed for modification.

### 3. Liskov Substitution Principle (LSP)
- All employee subtypes ([`FullTimeEmployee`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/model/FullTimeEmployee.java), [`PartTimeEmployee`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/model/PartTimeEmployee.java), [`ContractorEmployee`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/model/ContractorEmployee.java), [`InternEmployee`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/model/InternEmployee.java)) can be substituted wherever `Employee` is expected without runtime exceptions or type-checking (`instanceof`).

### 4. Interface Segregation Principle (ISP)
- Small, focused interfaces are created instead of one bloated interface:
  - [`EmployeeRepository`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/repository/EmployeeRepository.java)
  - [`PayrollService`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/service/PayrollService.java)
  - [`ReportExporter`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/report/ReportExporter.java)

### 5. Dependency Inversion Principle (DIP)
- High-level modules depend on abstractions (interfaces), not concrete implementations:
  - `PayrollServiceImpl` depends on `EmployeeRepository` and `ReportExporter`.
  - `PayrollCLI` depends on `PayrollService`.
- Concrete implementations are instantiated and injected in [`PayrollSystem.java`](file:///C:/Users/TrueID/Downloads/Employee-PayRoll/src/PayrollSystem.java).

---

## 🚀 Running the Refactored Application

```bash
# Compile
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java).FullName

# Run
java -cp out PayrollSystem
```
