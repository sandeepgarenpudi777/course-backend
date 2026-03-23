package com.course.registration.dto;

import com.course.registration.entity.Payment;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private Payment.PaymentStatus status;
    private LocalDateTime paymentDate;
    private String transactionId;
    private Payment.PaymentMethod paymentMethod;
    private Long enrollmentId;
}
