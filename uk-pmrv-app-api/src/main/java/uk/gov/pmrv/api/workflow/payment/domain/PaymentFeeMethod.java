package uk.gov.pmrv.api.workflow.payment.domain;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_payment_fee_method")
public class PaymentFeeMethod {

    @Id
    @SequenceGenerator(name = "request_payment_fee_method_id_generator", sequenceName = "request_payment_fee_method_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_payment_fee_method_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    private CompetentAuthorityEnum competentAuthority;

    @EqualsAndHashCode.Include()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private RequestType requestType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FeeMethodType type;

    @Builder.Default
    @ElementCollection
    @MapKeyColumn(name="type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name="amount")
    @CollectionTable(name = "request_payment_fee", joinColumns = @JoinColumn(name = "fee_method_id"))
    private Map<FeeType, BigDecimal> fees = new EnumMap<>(FeeType.class);
}
