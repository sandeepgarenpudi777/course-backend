package com.course.registration.serviceImpl;

import com.course.registration.dto.PaymentDTO;
import com.course.registration.entity.Enrollment;
import com.course.registration.entity.Payment;
import com.course.registration.exception.ResourceNotFoundException;
import com.course.registration.repository.EnrollmentRepository;
import com.course.registration.repository.PaymentRepository;
import com.course.registration.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        return toDTO(paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id)));
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO dto) {
        Enrollment enrollment = enrollmentRepository.findById(dto.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", dto.getEnrollmentId()));

        Payment payment = Payment.builder()
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(Payment.PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .transactionId(UUID.randomUUID().toString())
                .enrollment(enrollment)
                .build();

        // Confirm the enrollment after successful payment
        enrollment.setStatus(Enrollment.EnrollmentStatus.CONFIRMED);
        enrollmentRepository.save(enrollment);

        return toDTO(paymentRepository.save(payment));
    }

    @Override
    public PaymentDTO updatePayment(Long id, PaymentDTO dto) {
        Payment existing = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        existing.setStatus(dto.getStatus());
        if (dto.getPaymentMethod() != null) existing.setPaymentMethod(dto.getPaymentMethod());
        return toDTO(paymentRepository.save(existing));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByEnrollment(Long enrollmentId) {
        return toDTO(paymentRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "enrollmentId", enrollmentId)));
    }

    private PaymentDTO toDTO(Payment p) {
        return PaymentDTO.builder()
                .id(p.getId())
                .amount(p.getAmount())
                .status(p.getStatus())
                .paymentDate(p.getPaymentDate())
                .transactionId(p.getTransactionId())
                .paymentMethod(p.getPaymentMethod())
                .enrollmentId(p.getEnrollment() != null ? p.getEnrollment().getId() : null)
                .build();
    }
}
