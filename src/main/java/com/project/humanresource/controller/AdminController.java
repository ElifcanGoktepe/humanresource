    package com.project.humanresource.controller;

    import com.project.humanresource.dto.request.AddCompanyRequestDto;
    import com.project.humanresource.dto.response.BaseResponseShort;
    import com.project.humanresource.entity.CompanyBranch;
    import com.project.humanresource.entity.Department;
    import com.project.humanresource.entity.Employee;
    import com.project.humanresource.repository.UserRepository;
    import com.project.humanresource.service.*;
    import io.swagger.v3.oas.annotations.security.SecurityRequirement;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Optional;

    @RestController
    @CrossOrigin
    @RequiredArgsConstructor
    @RequestMapping("/dev/v1/admin/company")
    @SecurityRequirement(name = "bearerAuth")
    public class AdminController {

        private final EmailVerificationService emailVerificationService;
        private final EmployeeService employeeService;
        private final CompanyService companyService;
        private final CompanyBranchService companyBranchService;
        private final DepartmentService departmentService;
        private final AdminService adminService;
        private final UserRepository userRepository;

        @GetMapping("/pending-applications")
        public ResponseEntity<List<Employee>> getPendingManagerApplications() {
            List<Employee> pendingManagers = adminService.getPendingManagerApplications();
            return ResponseEntity.ok(pendingManagers);
        }

        @PutMapping("/update/{id}")
        public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
            Optional<Employee> optEmployee = employeeService.findById(id);
            if (optEmployee.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Employee employee = optEmployee.get();

            switch (status.toLowerCase()) {
                case "accept":
                    employee.setApproved(true);
                    employee.setActive(true);
                    employee.setActivated(true);
                    emailVerificationService.sendVerificationEmail(employee.getEmail());
                    employeeService.save(employee);
                    return ResponseEntity.ok("Başvuru kabul edildi.");

                case "pending":
                    employee.setApproved(false);
                    emailVerificationService.sendPendingNotificationEmail(employee.getEmail(), employee);
                    employeeService.save(employee);
                    return ResponseEntity.ok("Başvuru beklemeye alındı.");

                case "reject":
                    emailVerificationService.sendRejectionEmail(employee.getEmail(), employee);
                    employeeService.deleteById(employee.getId());
                    return ResponseEntity.ok("Başvuru reddedildi ve kayıt silindi.");

                default:
                    return ResponseEntity.badRequest().body("Geçersiz durum.");
            }
        }

        @GetMapping("/searchCompaniesByName")
        public ResponseEntity<List<AddCompanyRequestDto>> searchCompaniesByName(
                @RequestParam(required = false) String name) {
            List<AddCompanyRequestDto> result;
            if (name == null || name.isBlank()) {
                result = companyService.getAllCompanies();
            } else {
                result = companyService.searchCompaniesByName(name.trim());
            }
            return ResponseEntity.ok(result);
        }

        @PatchMapping("/updateCompanyEmail/{id}")
        public ResponseEntity<AddCompanyRequestDto> updateCompanyEmail(@PathVariable Long id, @RequestParam String email) {
            AddCompanyRequestDto updated = companyService.updateCompanyEmail(id, email);
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
        }

        @GetMapping("/getAllCompanies")
        public ResponseEntity<List<AddCompanyRequestDto>> getAllCompanies() {
            return ResponseEntity.ok(companyService.getAllCompanies());
        }

        @GetMapping("/getCompanyById/{id}")
        public ResponseEntity<AddCompanyRequestDto> getCompanyById(@PathVariable Long id) {
            AddCompanyRequestDto company = companyService.getCompanyById(id);
            return company != null ? ResponseEntity.ok(company) : ResponseEntity.notFound().build();
        }

        @PutMapping("/updateCompany/{id}")
        public ResponseEntity<AddCompanyRequestDto> updateCompany(@PathVariable Long id, @RequestBody AddCompanyRequestDto dto) {
            AddCompanyRequestDto updatedCompany = companyService.updateCompany(id, dto);
            return updatedCompany != null ? ResponseEntity.ok(updatedCompany) : ResponseEntity.notFound().build();
        }

        @DeleteMapping("/deleteCompany/{id}")
        public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
            companyService.deleteCompany(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/department/findById/{id}")
        public ResponseEntity<BaseResponseShort<Department>> findDepartmentById(@PathVariable Long id){
            Department department = departmentService.findById(id);
            return ResponseEntity.ok(BaseResponseShort.<Department>builder()
                    .data(department)
                    .code(200)
                    .message("Department found successfully.")
                    .build());
        }

        @GetMapping("/get_all_company_branches_of_selected_company/")
        public ResponseEntity<BaseResponseShort<List<CompanyBranch>>> getAllCompanyBranchesOfSelectedCompany(@RequestParam Long id) {
            List<CompanyBranch> companyBranches = companyBranchService.findAllCompanyBranchesOfSelectedCompany(id);
            return ResponseEntity.ok(BaseResponseShort.<List<CompanyBranch>>builder()
                    .data(companyBranches)
                    .code(200)
                    .message("Company Branches Found Successfully")
                    .build());
        }

        @GetMapping("/get_all_company_branches")
        public ResponseEntity<BaseResponseShort<List<CompanyBranch>>> getAllCompanyBranches() {
            List<CompanyBranch> branches = companyBranchService.findAllCompanyBranches();
            return ResponseEntity.ok(
                    BaseResponseShort.<List<CompanyBranch>>builder()
                            .data(branches)
                            .code(200)
                            .message("All Company Branches fetched successfully")
                            .build()
            );
        }
    }





