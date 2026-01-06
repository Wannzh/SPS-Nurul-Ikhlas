package com.sps.nurul_ikhlas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sps.nurul_ikhlas.models.entities.PaymentTransaction;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
    List<PaymentTransaction> findByStudentIdOrderByCreatedAtDesc(String studentId);

    Optional<PaymentTransaction> findByXenditInvoiceId(String xenditInvoiceId);
}
