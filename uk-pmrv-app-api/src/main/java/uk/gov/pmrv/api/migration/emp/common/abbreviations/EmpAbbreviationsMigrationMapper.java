package uk.gov.pmrv.api.migration.emp.common.abbreviations;

import java.util.List;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmpAbbreviationsMigrationMapper {

    List<EmpAbbreviationDefinition> toEmpAbbreviationDefinitions(List<EtsEmpAbbreviation> etsEmpAbbreviations);
}
