package com.demo.rippling.repository;

import com.demo.rippling.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    List<ProviderEntity> findByAgeBetween(int minAge, int maxAge);

    boolean existsByName(String name);
}
