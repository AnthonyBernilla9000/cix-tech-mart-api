package org.istrfa.utils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PredicateUtil {

    public static void addLikePredicate(List<Predicate> predicates, CriteriaBuilder cb, String value, Expression<String> expression) {
        if (Objects.nonNull(value)) {
            predicates.add(cb.like(cb.lower(expression), "%" + value.toLowerCase().trim() + "%"));
        }
    }

    public static void addEqualUuidPredicate(List<Predicate> predicates, CriteriaBuilder cb, UUID value, Expression<UUID> expression) {
        if (value != null) {
            predicates.add(cb.equal(expression, value));
        }
    }

    public static void addBetweenPredicate(List<Predicate> predicates, CriteriaBuilder cb, LocalDateTime startDate, LocalDateTime endDate, Expression<LocalDateTime> expression) {
        if (startDate != null && endDate != null) {
            predicates.add(cb.between(expression, startDate, endDate));
        }
    }

    public static void concatPerson(List<Predicate> predicates, String persona, CriteriaBuilder cb, Root root, String filtro) {
        if (Objects.nonNull(filtro)) {
            Expression<String> exp1 = cb.concat(cb.upper(root.get(persona).get("name")), " ");
            Expression<String> exp2 = cb.concat(cb.upper(root.get(persona).get("firstname")), " ");
            exp1 = cb.concat(exp1, exp2);
            exp1 = cb.concat(exp1, cb.upper(cb.upper(root.get(persona).get("lastname"))));

            predicates.add(cb.like(exp1, "%" + filtro.toUpperCase() + "%"));
        }
    }

    public static void concatClient(List<Predicate> predicates, CriteriaBuilder cb, Root root, String filtro) {
        if (Objects.nonNull(filtro)) {
            Expression<String> exp1 = cb.concat(cb.upper(root.get("name")), " ");
            Expression<String> exp2 = cb.concat(cb.upper(root.get("lastname")), " ");
            exp1 = cb.concat(exp1, exp2);

            predicates.add(cb.like(exp1, "%" + filtro.toUpperCase() + "%"));
        }
    }

    public static void concatNamePersonOrden(List<Predicate> predicates, CriteriaBuilder cb, Root root, String filtro) {
        if (Objects.nonNull(filtro)) {
            Expression<String> exp1 = cb.concat(cb.upper(root.get("firstname")), " ");
            Expression<String> exp2 = cb.concat(cb.upper(root.get("lastname")), " ");
            exp1 = cb.concat(exp1, exp2);

            predicates.add(cb.like(exp1, "%" + filtro.toUpperCase() + "%"));
        }
    }


    public static void addRangeDoublePredicate(List<Predicate> predicates, CriteriaBuilder cb, Double minValue, Double maxValue, Expression<Double> expression) {
        // Verificar si ambos valores están presentes
        if (Objects.nonNull(minValue) && Objects.nonNull(maxValue)) {
            // Si ambos valores están presentes, aplicar un filtro con ambos rangos
            predicates.add(cb.between(expression, minValue, maxValue));
        } else if (Objects.nonNull(minValue)) {
            // Si solo el valor mínimo está presente, aplicar un filtro para valores mayores o iguales
            predicates.add(cb.greaterThanOrEqualTo(expression, minValue));
        } else if (Objects.nonNull(maxValue)) {
            // Si solo el valor máximo está presente, aplicar un filtro para valores menores o iguales
            predicates.add(cb.lessThanOrEqualTo(expression, maxValue));
        }
    }


}
