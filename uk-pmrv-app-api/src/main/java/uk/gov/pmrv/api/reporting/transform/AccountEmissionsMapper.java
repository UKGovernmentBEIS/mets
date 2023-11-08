package uk.gov.pmrv.api.reporting.transform;

import org.mapstruct.Mapper;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsSaveParams;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountEmissionsMapper {

    ReportableEmissionsEntity toReportableEmissionsEntity(ReportableEmissionsSaveParams params);
}
