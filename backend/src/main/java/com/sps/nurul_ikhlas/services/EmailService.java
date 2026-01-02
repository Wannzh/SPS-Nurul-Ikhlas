package com.sps.nurul_ikhlas.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAccountCredentials(String toEmail, String studentName, String username, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Selamat! Pendaftaran " + studentName + " Diterima - SPS Nurul Ikhlas");
            message.setText(buildEmailBody(studentName, username, password));

            mailSender.send(message);
            log.info("Account credentials email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Gagal mengirim email: " + e.getMessage());
        }
    }

    private String buildEmailBody(String studentName, String username, String password) {
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
}
