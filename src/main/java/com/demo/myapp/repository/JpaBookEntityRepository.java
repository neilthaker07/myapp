package com.demo.myapp.repository;

import com.demo.myapp.repository.entity.BookEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data JPA — generates all SQL automatically from method names
// Only active when postgres profile is running
@Profile("postgres")
public interface JpaBookEntityRepository extends JpaRepository<BookEntity, Long> {
    boolean existsByTitleIgnoreCase(String title);
}
