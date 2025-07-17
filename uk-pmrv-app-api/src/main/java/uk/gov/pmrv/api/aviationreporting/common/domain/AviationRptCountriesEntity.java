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
import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.api.common.config.YearAttributeConverter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "aviation_rpt_countries",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"year", "country"})
    })
public class AviationRptCountriesEntity {

    @Id
    @SequenceGenerator(name = "aviation_rpt_countries_id_generator", sequenceName = "aviation_rpt_countries_seq",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aviation_rpt_countries_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    private Year year;

    @Column(name = "is_chapter_3")
    @NotNull
    private Boolean isChapter3;

    @EqualsAndHashCode.Include()
    @NotNull
    private String country;

    @NotNull
    private String state;
}
