package com.module.inventory.repository;

import com.module.inventory.entity.ExportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportRepository extends JpaRepository<ExportHistory,Long> {
}
