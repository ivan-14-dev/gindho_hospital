package com.gindho.payment.service;
import com.gindho.payment.model.*; import com.gindho.payment.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaiementTransactionRepository txRepo;
    @Mock private RemboursementRepository rembRepo;
    @InjectMocks private PaymentService paymentService;

    @Test
    void processPayment_GeneratesCode() {
        PaiementTransaction tx = new PaiementTransaction();
        tx.setMontant(BigDecimal.valueOf(25000));
        tx.setFactureId(1L);
        when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        PaiementTransaction result = paymentService.processPayment(tx);
        assertNotNull(result.getCodeTransaction());
        assertTrue(result.getCodeTransaction().startsWith("PAY-"));
    }

    @Test
    void refund_CreatesRemboursement() {
        when(rembRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        Remboursement result = paymentService.refund(1L, BigDecimal.valueOf(10000), "Annulation");
        assertNotNull(result);
        assertEquals(StatutRemboursement.EN_ATTENTE, result.getStatut());
    }
}