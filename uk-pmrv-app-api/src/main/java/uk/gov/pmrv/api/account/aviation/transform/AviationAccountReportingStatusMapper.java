package uk.gov.pmrv.api.account.aviation.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAccountReportingStatusMapper {

    AviationAccountReportingStatusDTO toReportingStatusDTO(AviationAccountReportingStatus accountReportingStatus, Boolean isReported);


}
