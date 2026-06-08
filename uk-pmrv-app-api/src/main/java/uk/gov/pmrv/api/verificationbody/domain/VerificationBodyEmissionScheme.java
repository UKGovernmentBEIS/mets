package uk.gov.pmrv.api.verificationbody.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

/**
 * The persistent class for the verification_body_emission_trading_scheme database table.
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners({AuditingEntityListener.class})
@Table(
        name = "verification_body_emission_trading_scheme",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "verification_body_id",
                        "emission_trading_scheme"
                })
        }
)
public class VerificationBodyEmissionScheme {

    @EqualsAndHashCode.Exclude
    @Id
    @SequenceGenerator(name = "verification_body_emission_trading_scheme_id_generator", sequenceName = "verification_body_emission_trading_scheme_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_body_emission_trading_scheme_id_generator")
    private Long id;

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "emission_trading_scheme")
    @NotNull
    private EmissionTradingScheme emissionTradingScheme;

    @EqualsAndHashCode.Include
    @Column(name = "accreditation_reference_number")
    @NotBlank
    private String accreditationReferenceNumber;

    @Column(name = "accreditation_name")
    private String accreditationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "verification_body_id", nullable = false)
    @NotNull
    private VerificationBody verificationBody;

}
