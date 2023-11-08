package uk.gov.pmrv.api.account.aviation.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AviationAccountReportingStatusHistoryMapper {

    AviationAccountReportingStatusHistoryDTO toReportingStatusHistoryDTO(AviationAccountReportingStatusHistory reportingStatusHistory);
}
