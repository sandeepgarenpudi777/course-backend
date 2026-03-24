package com.course.registration.service;

import com.course.registration.dto.PaymentDTO;
import java.util.List;

public interface PaymentService {
    List<PaymentDTO> getAllPayments();
    PaymentDTO getPaymentById(Long id);
    PaymentDTO createPayment(PaymentDTO dto);
    PaymentDTO updatePayment(Long id, PaymentDTO dto);
    PaymentDTO getPaymentByEnrollment(Long enrollmentId);
}
