package com.module.inventory.controller;


import com.module.inventory.entity.ExportHistory;
import com.module.inventory.response.RESTResponse;
import com.module.inventory.service.ExportServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/export/history")
public class ExportController {

    @Autowired
    ExportServiceImpl exportService;

    @RequestMapping(path = "create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody ExportHistory exportHistory){
        return new ResponseEntity(new RESTResponse.Success()
                .addData(exportService.creat(exportHistory))
                .build(),HttpStatus.OK);
    }
}
