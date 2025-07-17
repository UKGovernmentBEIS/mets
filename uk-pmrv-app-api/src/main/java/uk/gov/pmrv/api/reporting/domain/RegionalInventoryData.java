package uk.gov.pmrv.api.reporting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.config.YearAttributeConverter;

import java.math.BigDecimal;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rpt_regional_inventory_data")
public class RegionalInventoryData {

    @Id
    @SequenceGenerator(name = "rpt_regional_inventory_data_id_generator", sequenceName = "rpt_regional_inventory_data_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rpt_regional_inventory_data_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "reporting_year")
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    private Year reportingYear;

    @EqualsAndHashCode.Include()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_zone_id")
    private ChargingZone chargingZone;

    @Embedded
    @NotNull
    @Valid
    private EmissionCalculationParams emissionCalculationParams;

    @Column(name = "calculation_factor")
    @NotNull
    private BigDecimal calculationFactor;
}
