package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "emp_aircraft_type")
@SqlResultSetMapping(
    name = AircraftType.AIRCRAFT_TYPE_DTO_RESULT_MAPPER,
    classes = {
        @ConstructorResult(
            targetClass = AircraftTypeDTO.class,
            columns = {
                @ColumnResult(name = "manufacturer"),
                @ColumnResult(name = "model"),
                @ColumnResult(name = "designatorType")
            }
        )})
public class AircraftType {

    public static final String AIRCRAFT_TYPE_DTO_RESULT_MAPPER = "AircraftTypeDTOResultMapper";

    @EmbeddedId
    private AircraftTypeId aircraftTypeId;
}
