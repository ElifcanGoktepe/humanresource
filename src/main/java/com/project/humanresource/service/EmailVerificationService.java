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

        private final EmailVerificationRepository repository;
        private final EmployeeRepository employeeRepository;

        public void sendVerificationEmail(String toEmail) {
            Optional<Employee> optionalEmployee = employeeRepository.findByEmailWork(toEmail);
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
            // SMTP ayarlarının tanılanması:
            // Bu ayarlar, Gmail’in SMTP (Mail Gönderim) sunucusuna bağlanmak için gerekli.
            // TLS protokolü (güvenli bağlantı) ve kimlik doğrulama (auth) açılmış.
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            // Session.getInstance(...): Bu ayarlarla oturum (bağlantı ortamı) oluşturur
            // session: Artık bu session üzerinden e-posta gönderimi yapılabilir
            Session session = Session.getInstance(props, // props: SMTP sunucusu için gerekli ayarları içerir (host, port, TLS vb.)
                    new Authenticator() { // Authenticator: Sunucuya giriş için kullanıcı adı ve şifreyi sağlar
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("elifcangoktepe@gmail.com", "jynohncfzxegpmrz");
                        }
                    });
            /**
             * 🧠 Gerçek dünya benzetmesi:
             * 📫 E-posta göndermek bir posta ofisine gitmek gibidir.
             * props → hangi postaneye gideceğini (adres, güvenlik kuralı) belirler.
             * Authenticator → postanedeki hesabını göstermek için kimliğini (mail ve şifre) kullanır.
             * Session → bu kimlik ve kurallarla oraya bağlanmış bir "oturumdur", yani artık işlem yapmaya hazırsındır.
             */

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Email Verification");
                message.setText("Hello " + employee.getFirstName() + ",\n\n" +
                        "Click the link below to verify your email:\n\n" +
                        "http://localhost:9090/api/verify?token=" + token + "\n\n" +
                        "Best Regards,\nHumin Team");

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
        sendSetPasswordEmail(employee.getEmailWork(), token, employee);

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
        sendVerificationEmail(employee.getEmailWork());

        return true;
    }

    public void sendApprovalRequestToAdmin(Employee manager) {
        String subject = "New Company Manager Application";
        String approvalLink = "http://localhost:9090/approve/" + manager.getId();
        String body = "Hello Admin,\n\n" +
                "There is a new company application:\n\n" +
                "Name: " + manager.getFirstName() + " " + manager.getLastName() + "\n" +
                "Email: " + manager.getEmailWork() + "\n\n" +
                "Company: " + manager.getCompanyName() + "\n" +
                "Title : CEO\n\n" +
                "Click to approve:\n" + approvalLink + "\n\n" +
                "Best Regards,\nHumin Team";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, "jynohncfzxegpmrz"); // app password
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("elifcangoktepe@gmail.com")); // admin mail
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // konsolda detaylı hata görmek için
        }
    }
    @Value("${spring.mail.password}")
    private String fromPassword;

    //Şifre oluşturma bağlantısı gönder
    public void sendSetPasswordEmail(String toEmail, String token, Employee employee) {
        System.out.println("A mail for setting password has sent: " + toEmail);
        String link = "http://localhost:5173/create-password?token=" + token;

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
            message.setText("Hello " + employee.getFirstName() + ",\n\n" +
                    "Click the link below to set your password:\n\n" +
                    link + "\n\n" +
                    "Best Regards,\nHumin Team");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



    public Optional<EmailVerification> findByToken(String token) {
            return repository.findByToken(token);
    }
}



