package com.project.humanresource.repository;

import com.project.humanresource.dto.response.LeaveResponseDto;
import com.project.humanresource.entity.Leave;
import com.project.humanresource.utility.StateTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployeeIdInAndState(List<Long> employeeIds, StateTypes stateTypes);


    List<Leave> findAllByEmployeeIdAndState(Long employeeId, StateTypes stateTypes);

    @Query(value = """
    SELECT 
        l.id, l.start_date, l.end_date, l.description,
        l.leave_type, l.state, l.employee_id,
        e.first_name, e.last_name
    FROM tblleave l
    JOIN tblemployee e ON l.employee_id = e.id
    WHERE e.manager_id = :managerId AND l.state = 'Pending_Approval'
""", nativeQuery = true)
    List<Object[]> findPendingLeavesRaw(@Param("managerId") Long managerId);
}
