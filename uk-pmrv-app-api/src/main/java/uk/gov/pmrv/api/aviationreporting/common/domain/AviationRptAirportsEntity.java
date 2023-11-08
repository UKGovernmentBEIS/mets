package uk.gov.pmrv.api.aviationreporting.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "aviation_rpt_airports",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"icao"})
    })
public class AviationRptAirportsEntity {

    @Id
    @SequenceGenerator(name = "aviation_rpt_airports_id_generator", sequenceName = "aviation_rpt_airports_seq",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aviation_rpt_airports_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "icao")
    @NotNull
    private String icao;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "country")
    @NotNull
    private String country;

    @Column(name = "country_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private CountryType countryType;
}
