package com.project.humanresource.repository;

import com.project.humanresource.entity.Leave;
import com.project.humanresource.utility.StateTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployeeIdInAndState(List<Long> employeeIds, StateTypes stateTypes);

    List<Leave> findAllByEmployeeIdAndState(Long employeeId, StateTypes stateTypes);
}
