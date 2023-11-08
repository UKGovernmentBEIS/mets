package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.converter.YearAttributeConverter;

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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rpt_national_inventory_data")

@NamedEntityGraph(name = "sector-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "sector")
        })
@NamedQuery(
        name = NationalInventoryData.NAMED_QUERY_FIND_BY_REPORTING_YEAR_AND_SECTOR_AND_FUEL,
        query = "select data "
                + "from NationalInventoryData data "
                + "inner join IpccSector sector on sector.id=data.sector.id and sector.name= :sector "
                + "where data.reportingYear = :year "
                + "and data.fuel = :fuel"
)
public class NationalInventoryData {
    public static final String NAMED_QUERY_FIND_BY_REPORTING_YEAR_AND_SECTOR_AND_FUEL = "NationalInventoryData.findByReportingYearAndSectorAndFuel";

    @Id
    @SequenceGenerator(name = "rpt_national_inventory_data_id_generator", sequenceName = "rpt_national_inventory_data_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rpt_national_inventory_data_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "reporting_year")
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    private Year reportingYear;

    @EqualsAndHashCode.Include
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private IpccSector sector;

    @EqualsAndHashCode.Include()
    @Column(name = "fuel")
    @NotNull
    private String fuel;

    @Embedded
    @NotNull
    @Valid
    private EmissionCalculationParams emissionCalculationParams;
}
