package by.andersen.employee.utils;

import by.andersen.employee.domain.Employee;
import by.andersen.employee.dto.employee.EmployeeFilterDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationUtils {
    private SpecificationUtils() {
    }

    public static <T> Specification<T> createSpecificationForEmployeeHierarchy(EmployeeFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addStringLikePredicate(predicates, criteriaBuilder, root, Employee.Fields.firstName, filter.getFirstName());
            addStringLikePredicate(predicates, criteriaBuilder, root, Employee.Fields.patronymic, filter.getPatronymic());
            addStringLikePredicate(predicates, criteriaBuilder, root, Employee.Fields.lastName, filter.getLastName());
            addStringLikePredicate(predicates, criteriaBuilder, root, Employee.Fields.email, filter.getEmail());
            addManagerEmailLikePredicate(predicates, criteriaBuilder, root, filter.getManagerEmail());

            addDatePredicate(predicates, criteriaBuilder, root.get(Employee.Fields.birthDate), filter.getBirthDateFrom(), filter.getBirthDateTo());
            addDatePredicate(predicates, criteriaBuilder, root.get(Employee.Fields.hireDate), filter.getHireDateFrom(), filter.getHireDateTo());

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static <T> void addStringLikePredicate(List<Predicate> predicates, CriteriaBuilder cb, Root<T> root,
                                                  String field, String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(cb.like(cb.lower(root.get(field)), value.toLowerCase() + "%"));
        }
    }

    public static <T> void addManagerEmailLikePredicate(List<Predicate> predicates, CriteriaBuilder cb, Root<T> root,
                                                        String managerEmail) {
        if (StringUtils.isNotBlank(managerEmail)) {
            predicates.add(cb.like(cb.lower(root.get(Employee.Fields.manager).get(Employee.Fields.email)),
                    managerEmail.toLowerCase() + "%"));
        }
    }

    public static void addDatePredicate(List<Predicate> predicates, CriteriaBuilder cb, Path<Instant> path, Instant from,
                                        Instant to) {
        if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, from));
        }
        if (to != null) {
            predicates.add(cb.lessThanOrEqualTo(path, to));
        }
    }
}
