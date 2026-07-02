package uk.gov.pmrv.api.verificationbody.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the verification_body database table.
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners({AuditingEntityListener.class})
@Table(name = "verification_body")
@NamedQuery(
        name = VerificationBody.NAMED_QUERY_FIND_BY_ID,
        query = "select vb from VerificationBody vb "
                + "where vb.id = :id ")
@NamedQuery(
        name = VerificationBody.NAMED_QUERY_FIND_ACTIVE_VER_BODIES_ACCREDITED_TO_EMISSION_TRADING_SCHEME,
        query = "select new uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO(vb.id, vb.name) "
                + "from VerificationBody vb "
                + "join vb.emissionSchemes ets "
                + "where ets.emissionTradingScheme = :emissionTradingScheme "
                + "and vb.status = 'ACTIVE'"
)
@NamedQuery(
        name = VerificationBody.NAMED_QUERY_IS_VER_BODY_ACCREDITED_TO_EMISSION_TRADING_SCHEME,
        query = "select count(vb) > 0 "
                + "from VerificationBody vb "
                + "join vb.emissionSchemes ets "
                + "where vb.id = :vbId "
                + "and ets.emissionTradingScheme = :emissionTradingScheme "
                + "and vb.status = 'ACTIVE'"
)
@NamedQuery(
        name = VerificationBody.NAMED_QUERY_IS_VER_BODY_WITH_VER_BODY_EMISSION_SCHEMES,
        query = "select vb "
                + "from VerificationBody vb "
                + "inner join VerificationBodyEmissionScheme vbem on vb.id = vbem.verificationBody.id "
                + "where vb.id = :vbId ")
public class VerificationBody {
    public static final String NAMED_QUERY_FIND_BY_ID = "VerificationBody.findById";
    public static final String NAMED_QUERY_FIND_ACTIVE_VER_BODIES_ACCREDITED_TO_EMISSION_TRADING_SCHEME = "VerificationBody.findActiveVerificationBodiesAccreditedToEmissionTradingScheme";
    public static final String NAMED_QUERY_IS_VER_BODY_ACCREDITED_TO_EMISSION_TRADING_SCHEME = "VerificationBody.isVerificationBodyAccreditedToEmissionTradingScheme";
    public static final String NAMED_QUERY_IS_VER_BODY_WITH_VER_BODY_EMISSION_SCHEMES = "VerificationBody.findVerificationBodyWithVerBodyEmissionSchemes";

    @EqualsAndHashCode.Exclude
    @Id
    @SequenceGenerator(name = "verification_body_id_generator", sequenceName = "verification_body_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_body_id_generator")
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private VerificationBodyStatus status;

    @Embedded
    @AttributeOverride(name="line1", column=@Column(name="addr_line1"))
    @AttributeOverride(name="line2", column=@Column(name="addr_line2"))
    @AttributeOverride(name="city", column=@Column(name="addr_city"))
    @AttributeOverride(name="country", column=@Column(name="addr_country"))
    @AttributeOverride(name="postcode", column=@Column(name="addr_postcode"))
    @NotNull
    @Valid
    private Address address;

    @EqualsAndHashCode.Exclude
    @NotNull
    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "verificationBody", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<VerificationBodyEmissionScheme> emissionSchemes = new HashSet<>();

    public void addEmissionScheme(VerificationBodyEmissionScheme scheme) {
        emissionSchemes.add(scheme);
        scheme.setVerificationBody(this);
    }

    public void removeEmissionScheme(VerificationBodyEmissionScheme scheme) {
        emissionSchemes.remove(scheme);
    }
}
