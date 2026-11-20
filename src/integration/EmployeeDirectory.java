package integration;

import model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Standard domain contract (Target Interface in Adapter Pattern)
 * for employee discovery and persistence.
 */
public interface EmployeeDirectory {

    Optional<Employee> findEmployeeById(int id);

    List<Employee> findAllEmployees();

    void saveEmployee(Employee employee);

    boolean deleteEmployee(int id);

    default boolean containsEmployee(int id) {
        return findEmployeeById(id).isPresent();
    }
}
