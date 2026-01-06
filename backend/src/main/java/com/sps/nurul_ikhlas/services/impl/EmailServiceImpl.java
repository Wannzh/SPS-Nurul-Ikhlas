package com.sps.nurul_ikhlas.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.sps.nurul_ikhlas.services.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void sendAccountCredentials(String toEmail, String studentName, String username, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Selamat! Pendaftaran " + studentName + " Diterima - SPS Nurul Ikhlas");
            helper.setText(buildCredentialsEmailBody(studentName, username, password), false);

            mailSender.send(message);
            log.info("Account credentials email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Gagal mengirim email: " + e.getMessage());
        }
    }

    @Override
    public void sendActivationLink(String toEmail, String studentName, String username, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Aktivasi Akun PPDB - " + studentName + " - SPS Nurul Ikhlas");
            helper.setText(buildActivationEmailBody(studentName, username, token), true);

            mailSender.send(message);
            log.info("Activation link email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send activation email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Gagal mengirim email aktivasi: " + e.getMessage());
        }
    }

    private String buildCredentialsEmailBody(String studentName, String username, String password) {
        return String.format("""
                Assalamu'alaikum Wr. Wb.

                Selamat! Putra/Putri Bapak/Ibu yang bernama %s telah DITERIMA
                sebagai peserta didik di SPS Nurul Ikhlas.

                Berikut adalah akun untuk mengakses sistem informasi sekolah:

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Username : %s
                Password : %s
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                Silakan login di aplikasi SPS Nurul Ikhlas untuk melihat
                informasi lebih lanjut mengenai jadwal dan pembayaran.

                Harap segera ganti password Anda setelah login pertama kali.

                Terima kasih.

                Wassalamu'alaikum Wr. Wb.

                Tim Administrasi
                SPS Nurul Ikhlas
                """, studentName, username, password);
    }

    private String buildActivationEmailBody(String studentName, String username, String token) {
        String activationUrl = frontendUrl + "/setup-password?token=" + token;

        return String.format(
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <style>
                                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                                .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                                .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                                .button { display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white !important; padding: 15px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; margin: 20px 0; }
                                .info-box { background: #fff; padding: 15px; border-left: 4px solid #667eea; margin: 15px 0; }
                                .footer { text-align: center; color: #888; font-size: 12px; margin-top: 20px; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <h1>🎉 Selamat!</h1>
                                    <p>Pendaftaran %s Telah Diverifikasi</p>
                                </div>
                                <div class="content">
                                    <p>Assalamu'alaikum Wr. Wb.</p>

                                    <p>Alhamdulillah, putra/putri Bapak/Ibu yang bernama <strong>%s</strong> telah <strong>DITERIMA</strong> sebagai peserta didik di SPS Nurul Ikhlas.</p>

                                    <div class="info-box">
                                        <strong>📧 Username Anda:</strong><br>
                                        %s
                                    </div>

                                    <p>Untuk mengaktifkan akun Anda, silakan klik tombol di bawah ini untuk membuat password:</p>

                                    <p style="text-align: center;">
                                        <a href="%s" class="button">🔐 Buat Password Sekarang</a>
                                    </p>

                                    <p style="font-size: 12px; color: #888;">
                                        Atau salin link berikut ke browser Anda:<br>
                                        <code style="background: #eee; padding: 5px; border-radius: 3px; word-break: break-all;">%s</code>
                                    </p>

                                    <p><strong>⚠️ Penting:</strong> Tautan ini bersifat rahasia dan hanya dapat digunakan satu kali. Jangan bagikan kepada siapapun.</p>

                                    <p>Wassalamu'alaikum Wr. Wb.</p>

                                    <p><strong>Tim Administrasi</strong><br>SPS Nurul Ikhlas</p>
                                </div>
                                <div class="footer">
                                    <p>Email ini dikirim secara otomatis. Mohon tidak membalas email ini.</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                studentName, studentName, username, activationUrl, activationUrl);
    }
}
