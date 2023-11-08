package uk.gov.pmrv.api.workflow.request.flow.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

class EmissionTradingSchemeToEntityDocumentTemplateTypeMapperTest {

	private final EmissionTradingSchemeToEntityDocumentTemplateTypeMapper cut = Mappers.getMapper(EmissionTradingSchemeToEntityDocumentTemplateTypeMapper.class);

    @Test
    void map() {
    	assertThat(cut.map(EmissionTradingScheme.UK_ETS_INSTALLATIONS)).isEqualTo(DocumentTemplateType.PERMIT);
    	assertThat(cut.map(EmissionTradingScheme.UK_ETS_AVIATION)).isEqualTo(DocumentTemplateType.EMP_UKETS);
    	assertThat(cut.map(EmissionTradingScheme.CORSIA)).isEqualTo(DocumentTemplateType.EMP_CORSIA);
    	assertThat(cut.map(EmissionTradingScheme.EU_ETS_INSTALLATIONS)).isEqualTo(null);
    }
    
}
