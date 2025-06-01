package com.project.humanresource.service;

import com.project.humanresource.entity.EmailVerification;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.EmailVerificationRepository;
import com.project.humanresource.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String fromPassword;

    private final EmailVerificationRepository repository;
    private final EmployeeRepository employeeRepository;

    public void sendVerificationEmail(String toEmail) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(toEmail);
        if (optionalEmployee.isEmpty()) {
            throw new RuntimeException("User not found: " + toEmail);
        }

        Employee employee = optionalEmployee.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

        EmailVerification verification = new EmailVerification();
        verification.setEmail(toEmail);
        verification.setToken(token);
        verification.setExpiryDate(expiryDate);
        verification.setEmployeeId(employee.getId());
        repository.save(verification);

        String subject = "Email Verification";
        String body = "Hello " + employee.getFirstName() + ",\n\n" +
                "Click the link below to verify your email:\n" +
                "http://localhost:9090/api/verify?token=" + token + "\n\n" +
                "Best Regards,\nHumin Team";

        sendSimpleEmail(toEmail, subject, body);
    }

    public boolean verifyToken(String token) {
        Optional<EmailVerification> optional = repository.findByToken(token);
        if (optional.isEmpty()) return false;

        EmailVerification verification = optional.get();

        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) return false;

        Long employeeId = verification.getEmployeeId();
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) return false;

        Employee employee = employeeOpt.get();

        if (!employee.isApproved()) return false;

        employee.setActivated(true);
        employeeRepository.save(employee);


        sendSetPasswordEmail(employee.getEmail(), token, employee);

        return true;
    }

    public boolean approveCompanyManager(Long employeeId) {
        Optional<Employee> optional = employeeRepository.findById(employeeId);
        if (optional.isEmpty()) return false;

        Employee employee = optional.get();

        employee.setApproved(true);
        employeeRepository.save(employee);


        sendVerificationEmail(employee.getEmail());

        return true;
    }

    public void sendApprovalRequestToAdmin(Employee manager) {
        String subject = "New Company Manager Application";
        String approvalLink = "http://localhost:9090/approve/" + manager.getId();
        String body = "Hello Admin,\n\n" +
                "There is a new company application:\n\n" +
                "Name: " + manager.getFirstName() + " " + manager.getLastName() + "\n" +
                "Email: " + manager.getEmail() + "\n\n" +
                "Company: " + manager.getCompanyName() + "\n" +
                "Title : CEO\n\n" +
                "Click to approve:\n" + approvalLink + "\n\n" +
                "Best Regards,\nHumin Team";


        sendSimpleEmail("serkan.klcdr@gmail.com", subject, body);
    }

    public void sendSetPasswordEmail(String toEmail, String token, Employee employee) {
        String subject = "Create Password";
        String link = "http://localhost:5173/create-password?token=" + token;
        String body = "Hello " + employee.getFirstName() + ",\n\n" +
                "Click the link below to set your password:\n" +
                link + "\n\n" +
                "Best Regards,\nHumin Team";

        sendSimpleEmail(toEmail, subject, body);
    }

    public void sendRejectionEmail(String toEmail, Employee employee) {
        String subject = "Application Rejected";
        String body = "Hello " + employee.getFirstName() + ",\n\n" +
                "We regret to inform you that your application has been rejected.\n\n" +
                "Best Regards,\nHumin Team";

        sendSimpleEmail(toEmail, subject, body);
    }

    public void sendPendingNotificationEmail(String toEmail, Employee employee) {
        String subject = "Application Under Review";
        String body = "Hello " + employee.getFirstName() + ",\n\n" +
                "Your application is currently under review. We will notify you once a decision is made.\n\n" +
                "Best Regards,\nHumin Team";

        sendSimpleEmail(toEmail, subject, body);
    }

    private void sendSimpleEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + toEmail, e);
        }
    }

    public Optional<EmailVerification> findByToken(String token) {
        return repository.findByToken(token);
    }
}
