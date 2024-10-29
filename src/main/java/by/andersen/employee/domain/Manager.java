package by.andersen.employee.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "managers")
public class Manager extends Employee {
    @OneToMany(mappedBy = "manager", cascade = CascadeType.PERSIST)
    private List<Employee> subordinates = new ArrayList<>();

    public void addSubordinate(Employee employee) {
        subordinates.add(employee);
        employee.setManager(this);
    }

    public void removeSubordinate(Employee employee) {
        subordinates.remove(employee);
        employee.setManager(null);    }
}