package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmissionTradingSchemeToReissueDocumentTemplateTypeMapper {

	 @ValueMapping(target = "PERMIT_REISSUE", source = "UK_ETS_INSTALLATIONS")
	 @ValueMapping(target = "EMP_REISSUE_UKETS", source = "UK_ETS_AVIATION")
	 @ValueMapping(target = "EMP_REISSUE_CORSIA", source = "CORSIA")
	 @ValueMapping(target = MappingConstants.NULL, source = MappingConstants.ANY_UNMAPPED) // TODO: consider throwing exception instead (after upgrade to mapstruct 1.5)
	 DocumentTemplateType map(EmissionTradingScheme emissionTradingScheme);
	 
}
