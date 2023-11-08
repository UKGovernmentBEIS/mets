package uk.gov.pmrv.api.aviationreporting.common.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import uk.gov.pmrv.api.common.domain.converter.YearAttributeConverter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "aviation_rpt_aer")
public class AviationAerEntity {

    @Id
    private String id;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    @Valid
    private AviationAerContainer aerContainer;

    @Column(name = "account_id")
    @NotNull
    @EqualsAndHashCode.Include()
    private Long accountId;

    @Column(name = "year")
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    @EqualsAndHashCode.Include()
    private Year year;

    @Column(name = "reportable_emissions", updatable = false, insertable = false)
    private BigDecimal reportableEmissions;
}
