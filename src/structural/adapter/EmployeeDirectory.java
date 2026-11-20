package structural.adapter;

import model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Target Interface (GoF Adapter Pattern).
 *
 * Defines the standard domain contract expected by the Payroll Service
 * to discover and query employee details, independent of whether the underlying
 * data source is an internal database, local repository, or third-party HR API.
 */
public interface EmployeeDirectory {

    /**
     * Finds an employee by their internal integer ID.
     *
     * @param id internal employee ID
     * @return Optional containing the Employee if found and active, empty otherwise
     */
    Optional<Employee> findEmployeeById(int id);

    /**
     * Retrieves all active employees available in the directory.
     *
     * @return list of Employee domain objects
     */
    List<Employee> findAllEmployees();

    /**
     * Checks if an employee exists and is active.
     *
     * @param id internal employee ID
     * @return true if employee exists and is active
     */
    default boolean containsEmployee(int id) {
        return findEmployeeById(id).isPresent();
    }
}
