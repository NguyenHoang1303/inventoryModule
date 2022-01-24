package com.module.inventory.service;

import com.module.inventory.entity.ImportHistory;
import org.springframework.transaction.annotation.Transactional;

public interface ImportService {
    @Transactional
    ImportHistory save(ImportHistory importHistory);
}
