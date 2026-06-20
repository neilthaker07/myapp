package com.demo.myapp.specification;

// Specification Pattern — encapsulates a business rule as a composable predicate
// Generic so it works for any domain type, not just Book
public interface Specification<T> {

    boolean isSatisfiedBy(T candidate);

    // Combine with AND — both rules must pass
    default Specification<T> and(Specification<T> other) {
        return candidate -> this.isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
    }

    // Combine with OR — either rule must pass
    default Specification<T> or(Specification<T> other) {
        return candidate -> this.isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
    }

    // Negate — rule must NOT pass
    default Specification<T> not() {
        return candidate -> !this.isSatisfiedBy(candidate);
    }
}
