package com.project.humanresource.repository;

import com.project.humanresource.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository <Company , Long> {

}
