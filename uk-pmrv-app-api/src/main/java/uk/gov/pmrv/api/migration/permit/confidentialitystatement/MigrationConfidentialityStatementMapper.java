package uk.gov.pmrv.api.migration.permit.confidentialitystatement;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialSection;

import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationConfidentialityStatementMapper {

    @Mapping(target = "explanation", source = "justification")
    ConfidentialSection toConfidentialSection(EtsConfidentialityStatement etsConfidentialityStatement);

    List<ConfidentialSection> toConfidentialSections(List<EtsConfidentialityStatement> etsConfidentialityStatements);
}
