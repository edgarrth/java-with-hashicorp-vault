package com.edgar.vaultpoc.infrastructure.rest;
import com.edgar.vaultpoc.application.usecase.*;import com.edgar.vaultpoc.domain.model.Payment;import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.math.BigDecimal;import java.util.UUID;
@RestController @RequestMapping("/payments/v1/payments")
public class PaymentController{
 private final CreatePaymentUseCase create; private final ReadPaymentUseCase read;
 public PaymentController(CreatePaymentUseCase c,ReadPaymentUseCase r){this.create=c;this.read=r;}
 @PostMapping public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest req){Payment p=create.execute(req.merchantId(),req.amount(),req.currency(),req.cardPan());return ResponseEntity.status(201).body(PaymentResponse.from(p));}
 @GetMapping("/{paymentId}") public ResponseEntity<PaymentResponse> get(@PathVariable UUID paymentId){return read.execute(paymentId).map(p->ResponseEntity.ok(PaymentResponse.from(p))).orElse(ResponseEntity.notFound().build());}
 public record CreatePaymentRequest(@NotBlank String merchantId,@NotNull @Positive BigDecimal amount,@NotBlank String currency,@NotBlank String cardPan){}
 public record PaymentResponse(UUID id,String merchantId,BigDecimal amount,String currency,String maskedCard,String status,String createdAt){static PaymentResponse from(Payment p){return new PaymentResponse(p.id(),p.merchantId(),p.amount(),p.currency(),p.maskedCard(),p.status().name(),p.createdAt().toString());}}
}
