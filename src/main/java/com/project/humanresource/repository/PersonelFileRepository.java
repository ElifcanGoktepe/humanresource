package com.project.humanresource.repository;

import com.project.humanresource.entity.PersonalFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonelFileRepository extends JpaRepository<PersonalFile,Long> {

    Optional<PersonalFile> findByEmployeeId(long employeeId);       //

    boolean existsByEmployeeId(Long employeeId);
}
