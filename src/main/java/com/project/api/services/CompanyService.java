package com.project.api.services;

import com.project.api.entities.Company;
import com.project.api.repositories.CompanyRepository;
import com.project.api.services.base.BaseService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true) // works only on public methods
@Service
@RequiredArgsConstructor
public class CompanyService extends BaseService<Company, Long> {

  @Getter private final CompanyRepository repository;
}
