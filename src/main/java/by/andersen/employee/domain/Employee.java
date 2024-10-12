package by.andersen.employee.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "employees", indexes = @Index(columnList = "email", name = "idx_employee_email"))
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    private String patronymic;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private Instant birthDate;

    @Column(name = "hire_date")
    private Instant hireDate;

    @ManyToOne
    @JoinColumn(name = "manager_id",
            foreignKey = @ForeignKey(name = "employee_id_fk")
    )
    private Manager manager;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Employee employee)) return false;
        return Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}