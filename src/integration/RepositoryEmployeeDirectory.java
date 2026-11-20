package integration;

import model.Employee;
import repository.EmployeeRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Alternate implementation of {@link EmployeeDirectory} backed by local {@link EmployeeRepository}.
 */
public class RepositoryEmployeeDirectory implements EmployeeDirectory {
    private final EmployeeRepository repository;

    public RepositoryEmployeeDirectory(EmployeeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "EmployeeRepository must not be null");
    }

    @Override
    public Optional<Employee> findEmployeeById(int id) {
        return repository.findById(id);
    }

    @Override
    public List<Employee> findAllEmployees() {
        return repository.findAll();
    }

    @Override
    public void saveEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        repository.save(employee);
    }

    @Override
    public boolean deleteEmployee(int id) {
        return repository.deleteById(id);
    }
}
