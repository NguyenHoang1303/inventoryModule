package com.module.inventory.repository;

import com.module.inventory.entity.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportRepository extends JpaRepository<ImportHistory,Long> {
}
