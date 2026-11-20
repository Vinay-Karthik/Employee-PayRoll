package repository;

import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Concrete implementation of EmployeeRepository using CSV file storage.
 * Encapsulates all I/O details, serialization, and deserialization.
 */
public class CsvEmployeeRepository implements EmployeeRepository {
    private static final String CSV_HEADER = "# id,name,email,type,baseSalary,hourlyRate,hoursWorked,bonus,stipend,department,bankAccount";
    private final Path filePath;
    private final Map<Integer, Employee> cache = new LinkedHashMap<>();

    public CsvEmployeeRepository(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "File path must not be null");
        loadFromFile();
    }

    private void loadFromFile() {
        cache.clear();
        if (!Files.exists(filePath)) {
            seedInitialData();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                Employee emp = parseCsvLine(line);
                if (emp != null) {
                    cache.put(emp.getId(), emp);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Failed to read employee records: " + e.getMessage());
        }
    }

    private Employee parseCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 11) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            String email = parts[2].trim();
            int type = Integer.parseInt(parts[3].trim());
            double baseSalary = Double.parseDouble(parts[4].trim());
            double hourlyRate = Double.parseDouble(parts[5].trim());
            double hoursWorked = Double.parseDouble(parts[6].trim());
            double bonus = Double.parseDouble(parts[7].trim());
            double stipend = Double.parseDouble(parts[8].trim());
            String department = parts[9].trim();
            String bankAccount = parts[10].trim();

            return switch (type) {
                case 1 -> new FullTimeEmployee(id, name, email, department, bankAccount, baseSalary, bonus);
                case 2 -> new PartTimeEmployee(id, name, email, department, bankAccount, hourlyRate, hoursWorked);
                case 3 -> new ContractorEmployee(id, name, email, department, bankAccount, hourlyRate, hoursWorked);
                case 4 -> new InternEmployee(id, name, email, department, bankAccount, stipend);
                default -> null;
            };
        } catch (Exception e) {
            System.err.println("Skipping malformed CSV line: " + line);
            return null;
        }
    }

    private void seedInitialData() {
        save(new FullTimeEmployee(101, "Alice Johnson", "alice@company.com", "Engineering", "US-112233", 6500.0, 800.0));
        save(new PartTimeEmployee(102, "Bob Smith", "bob@company.com", "Operations", "US-445566", 35.0, 48.0));
        save(new ContractorEmployee(103, "Charlie Davis", "charlie@company.com", "IT Consulting", "US-778899", 75.0, 60.0));
        save(new InternEmployee(104, "Diana Prince", "diana@company.com", "Marketing", "US-990011", 2500.0));
    }

    private void flushToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(CSV_HEADER);
            writer.newLine();

            for (Employee emp : cache.values()) {
                double baseSalary = (emp instanceof FullTimeEmployee ft) ? ft.getBaseSalary() : 0.0;
                double bonus = (emp instanceof FullTimeEmployee ft) ? ft.getBonus() : 0.0;
                double hourlyRate = (emp instanceof PartTimeEmployee pt) ? pt.getHourlyRate()
                        : (emp instanceof ContractorEmployee c) ? c.getHourlyRate() : 0.0;
                double hoursWorked = (emp instanceof PartTimeEmployee pt) ? pt.getHoursWorked()
                        : (emp instanceof ContractorEmployee c) ? c.getHoursWorked() : 0.0;
                double stipend = (emp instanceof InternEmployee in) ? in.getStipend() : 0.0;

                String row = String.format("%d,%s,%s,%d,%.1f,%.1f,%.1f,%.1f,%.1f,%s,%s",
                        emp.getId(), emp.getName(), emp.getEmail(), emp.getTypeCode(),
                        baseSalary, hourlyRate, hoursWorked, bonus, stipend,
                        emp.getDepartment(), emp.getBankAccountNumber());

                writer.write(row);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist employees to " + filePath, e);
        }
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public void save(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        cache.put(employee.getId(), employee);
        flushToFile();
    }

    @Override
    public boolean deleteById(int id) {
        if (cache.containsKey(id)) {
            cache.remove(id);
            flushToFile();
            return true;
        }
        return false;
    }
}
