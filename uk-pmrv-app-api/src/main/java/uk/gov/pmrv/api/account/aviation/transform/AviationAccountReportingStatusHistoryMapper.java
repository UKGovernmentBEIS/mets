package uk.gov.pmrv.api.account.aviation.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAccountReportingStatusHistoryMapper {

    AviationAccountReportingStatusHistoryDTO toReportingStatusHistoryDTO(AviationAccountReportingStatusHistory reportingStatusHistory);
}
