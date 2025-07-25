package uk.gov.pmrv.api.workflow.request.flow.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

/**
 * Entity: Permit/EMP document
 *
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmissionTradingSchemeToEntityDocumentTemplateTypeMapper {

	 @ValueMapping(target = "PERMIT", source = "UK_ETS_INSTALLATIONS")
	 @ValueMapping(target = "EMP_UKETS", source = "UK_ETS_AVIATION")
	 @ValueMapping(target = "EMP_CORSIA", source = "CORSIA")
	 @ValueMapping(target = MappingConstants.NULL, source = MappingConstants.ANY_UNMAPPED) // TODO: consider throwing exception instead (after upgrade to mapstruct 1.5)
	 DocumentTemplateType map(EmissionTradingScheme emissionTradingScheme);
	 
}
