package com.peri.psd.testapp.playground.api;

import com.peri.psd.testapp.playground.dto.CompanyMasterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class PsdTestController {


    @GetMapping("/{dbName}/company/{id}")
    public ResponseEntity<CompanyMasterDto> getCompanyById(
        @PathVariable String dbName,
        @PathVariable Long id) {

        log.debug("GET: /{}/company/{}", dbName, id);

        return ResponseEntity.ok(CompanyMasterDto.builder()
                                     .id(id)
                                     .name(dbName + "-Name")
                                     .city(dbName + "-City")
                                     .street(dbName + "-Street")
                                     .postalCode(dbName + "-PostalCode")
                                     .build());
    }

    @GetMapping("/{dbName}/company")
    public ResponseEntity<List<CompanyMasterDto>> getAllCompanies(@PathVariable String dbName) {

        log.debug("GET: /{}/company", dbName);
        return ResponseEntity.ok(List.of(CompanyMasterDto.builder()
                                             .id(System.currentTimeMillis())
                                             .name(dbName + "-Name")
                                             .city(dbName + "-City")
                                             .street(dbName + "-Street")
                                             .postalCode(dbName + "-PostalCode")
                                             .build(),
                                         CompanyMasterDto.builder()
                                             .id(System.currentTimeMillis())
                                             .name(dbName + "-Name")
                                             .city(dbName + "-City")
                                             .street(dbName + "-Street")
                                             .postalCode(dbName + "-PostalCode")
                                             .build()));
    }

    @PostMapping("/{dbName}/company")
    public ResponseEntity<CompanyMasterDto> createCompany(@PathVariable String dbName,
        @RequestBody CompanyMasterDto companyMasterDto) {

        log.debug("POST: /{}/company - {}", dbName, companyMasterDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(companyMasterDto);
    }

    @PutMapping("/{dbName}/company")
    public ResponseEntity<CompanyMasterDto> updateCompany(@PathVariable String dbName,
        @RequestBody CompanyMasterDto companyMasterDto) {

        log.debug("PUT: /{}/company - {}", dbName, companyMasterDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(companyMasterDto);
    }

}
