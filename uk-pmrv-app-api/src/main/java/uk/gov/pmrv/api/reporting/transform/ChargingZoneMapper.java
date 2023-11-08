package uk.gov.pmrv.api.reporting.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.reporting.domain.ChargingZone;
import uk.gov.pmrv.api.reporting.domain.dto.ChargingZoneDTO;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface ChargingZoneMapper {

    ChargingZoneDTO toChargingZoneDTO(ChargingZone chargingZone);

    List<ChargingZoneDTO> toChargingZoneDTOs(List<ChargingZone> chargingZones);
}
