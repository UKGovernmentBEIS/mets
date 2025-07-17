package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmissionTradingSchemeToOfficialNoticeFileNameMapper {

	 @ValueMapping(target = "Batch_variation_notice.pdf", source = "UK_ETS_INSTALLATIONS")
	 @ValueMapping(target = "Batch_variation_notice_UK_ETS.pdf", source = "UK_ETS_AVIATION")
	 @ValueMapping(target = "Batch_variation_notice_CORSIA.pdf", source = "CORSIA")
	 @ValueMapping(target = MappingConstants.NULL, source = MappingConstants.ANY_UNMAPPED) // TODO: consider throwing exception instead (after upgrade to mapstruct 1.5)
	 String map(EmissionTradingScheme emissionTradingScheme);
	 
}
