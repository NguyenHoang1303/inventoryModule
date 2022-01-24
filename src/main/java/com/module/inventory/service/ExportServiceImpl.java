package com.module.inventory.service;

import com.module.inventory.entity.ExportHistory;
import com.module.inventory.repository.ExportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportServiceImpl {

    @Autowired
    ExportRepository exportRepository;

    public ExportHistory creat(ExportHistory exportHistory){
        return exportRepository.save(exportHistory);
    }

}
