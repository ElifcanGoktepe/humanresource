package com.project.humanresource.repository;

import com.project.humanresource.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByCompanyId(Long companyId);

    List<Shift> findAllByCompanyIdAndStartTimeBetween(Long companyId, LocalDateTime start, LocalDateTime end);

}
