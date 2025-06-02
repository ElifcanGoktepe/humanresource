package com.project.humanresource.repository;

import com.project.humanresource.entity.Leave;
import com.project.humanresource.utility.StateTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployeeIdInAndState(List<Long> employeeIds, StateTypes stateTypes);

    List<Leave> findAllByEmployeeIdAndState(Long employeeId, StateTypes stateTypes);

    @Query("SELECT COALESCE(SUM(l.leaveAssigned), 0) FROM Leave l WHERE l.employeeId = :employeeId")
    Long getAssignedLeaveDays(@Param("employeeId") Long employeeId);

    List<Leave> findAllByEmployeeId(Long employeeId);

    @Query("SELECT COUNT(l) FROM Leave l WHERE l.employeeId = :employeeId AND l.state = 'Approved'")
    Long getUsedLeaveDays(@Param("employeeId") Long employeeId);
}
