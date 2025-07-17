package uk.gov.pmrv.api.aviationreporting.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.api.common.config.YearAttributeConverter;

import java.math.BigDecimal;
import java.time.Year;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "aviation_rpt_reportable_emissions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_id", "year"})
    })
public class AviationReportableEmissionsEntity {

    @Id
    @SequenceGenerator(name = "aviation_rpt_reportable_emissions_id_generator", sequenceName = "aviation_rpt_reportable_emissions_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aviation_rpt_reportable_emissions_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "account_id")
    @NotNull
    private Long accountId;

    @EqualsAndHashCode.Include()
    @Column(name = "year")
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    private Year year;

    @Column(name = "emissions")
    @NotNull
    private BigDecimal reportableEmissions;

    @Column(name = "offset_emissions")
    private BigDecimal reportableOffsetEmissions;

    @Column(name = "reduction_claim_emissions")
    private BigDecimal reportableReductionClaimEmissions;

    @Column(name = "is_from_dre", columnDefinition = "boolean default false")
    private boolean isFromDre;

    @Column(name = "is_exempted", columnDefinition = "boolean default false")
    private boolean isExempted;
}
