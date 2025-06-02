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

            // email verification nesnesi oluşturuluyor
            EmailVerification verification = new EmailVerification();
            verification.setEmail(toEmail);
            verification.setToken(token);
            verification.setExpiryDate(expiryDate);
            verification.setEmployeeId(employee.getId());
            repository.save(verification);

            sendEmail(toEmail, token, employee);
        }

    private void sendEmail(String toEmail, String token, Employee employee) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("elifcangoktepe@gmail.com", "jynohncfzxegpmrz");
            }
        });

        try {
            String verifyLink = "http://localhost:9090/api/verify?token=" + token;

            // 🔵 HTML gövdeli mail içeriği
            String htmlBody = "<p>Hello " + employee.getFirstName() + ",</p>" +
                    "<p>Click the button below to verify your email:</p>" +
                    "<a href=\"" + verifyLink + "\" style=\"" +
                    "display: inline-block;" +
                    "padding: 10px 20px;" +
                    "background-color: #00796B;" +
                    "color: white;" +
                    "text-decoration: none;" +
                    "border-radius: 5px;" +
                    "font-weight: bold;" +
                    "margin-top: 10px;\">" +
                    "Verify Email</a>" +
                    "<p style=\"margin-top: 20px;\">Best Regards,<br>Humin Team</p>";

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("elifcangoktepe@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Email Verification");

            // ✨ HTML formatlı içerik gönderiyoruz
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyToken(String token) {
        Optional<EmailVerification> optional = repository.findByToken(token);
        if (optional.isEmpty()) return false;

        EmailVerification verification = optional.get();

        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) return false;

        Long employeeId = verification.getEmployeeId(); // ID al
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) return false;

        Employee employee = employeeOpt.get();

        if (!employee.isApproved()) return false; // admin onayı kontrolü

        employee.setActivated(true); // ✅ email doğrulama tamam
        employeeRepository.save(employee);

        // burada kullanıcıya parola oluşturma linki gönderilebilir
        sendSetPasswordEmail(employee.getEmail(), token, employee);

        return true;
    }
    // admin linke tıkladığında bu adrese gelir ve böylelikle isApproved = true olur
    public boolean approveCompanyManager(Long employeeId) {
        Optional<Employee> optional = employeeRepository.findById(employeeId);
        if (optional.isEmpty()) return false;

        Employee employee = optional.get();

        // Admin onayı
        employee.setApproved(true);
        employeeRepository.save(employee);

        // ✅ Şimdi doğrulama maili gönder
        sendVerificationEmail(employee.getEmail());

        return true;
    }

    public void sendApprovalRequestToAdmin(Employee manager, String token) {
        String subject = "New Company Manager Application";

        // 🔐 Token ekli endpoint
        String approvalLink = "http://localhost:9090/approve/" + manager.getId() + "?token=" + token;

        // 💌 HTML içerikli e-posta gövdesi
        String htmlBody = "<p>Hello Admin,</p>" +
                "<p>There is a new company manager application:</p>" +
                "<ul>" +
                "<li><strong>Name:</strong> " + manager.getFirstName() + " " + manager.getLastName() + "</li>" +
                "<li><strong>Email:</strong> " + manager.getEmail() + "</li>" +
                "<li><strong>Company:</strong> " + manager.getCompanyName() + "</li>" +
                "<li><strong>Title:</strong> CEO</li>" +
                "</ul>" +
                "<p>Click the button below to approve:</p>" +
                "<a href=\"" + approvalLink + "\" style=\"display:inline-block; padding:10px 20px; background-color:#00796B; color:white; text-decoration:none; border-radius:5px;\">Approve</a>" +
                "<p><br/>Best Regards,<br/>Humin Team</p>";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword); // app password
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("elifcangoktepe@gmail.com"));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //Şifre oluşturma bağlantısı gönder
    public void sendSetPasswordEmail(String toEmail, String token, Employee employee) {
        String subject = "Create Password";
        String link = "http://localhost:5173/create-password?token=" + token;

        String htmlBody = "<p>Hello " + employee.getFirstName() + ",</p>" +
                "<p>Click the button below to set your password:</p>" +
                "<a href=\"" + link + "\" style=\"" +
                "display: inline-block;" +
                "padding: 10px 20px;" +
                "background-color: #00796B;" +
                "color: white;" +
                "text-decoration: none;" +
                "border-radius: 5px;" +
                "font-weight: bold;\">" +
                "Set Password</a>" +
                "<p style=\"margin-top: 20px;\">Best Regards,<br>Humin Team</p>";

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
            message.setSubject("Create Password");

            // ✨ HTML içerik olarak gönder
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
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



