package uk.gov.pmrv.api.settings.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.settings.domain.enumeration.FeeHistoryActionType;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;

@Entity
@Table(name = "request_payment_fee_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeHistory {

    @Id
    @SequenceGenerator(name = "request_payment_fee_history_id_generator", sequenceName = "request_payment_fee_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_payment_fee_history_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_method_id", nullable = false)
    private PaymentFeeMethod feeMethod;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private FeeHistoryActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(name = "old_amount", nullable = false)
    private BigDecimal oldAmount;

    @Column(name = "new_amount", nullable = false)
    private BigDecimal newAmount;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;
}
