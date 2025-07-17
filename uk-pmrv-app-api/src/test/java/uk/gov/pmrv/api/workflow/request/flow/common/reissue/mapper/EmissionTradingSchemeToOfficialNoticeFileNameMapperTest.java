package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

class EmissionTradingSchemeToOfficialNoticeFileNameMapperTest {

	private final EmissionTradingSchemeToOfficialNoticeFileNameMapper cut = Mappers.getMapper(EmissionTradingSchemeToOfficialNoticeFileNameMapper.class);

	@Test
    void map() {
    	assertThat(cut.map(EmissionTradingScheme.UK_ETS_INSTALLATIONS)).isEqualTo("Batch_variation_notice.pdf");
    	assertThat(cut.map(EmissionTradingScheme.UK_ETS_AVIATION)).isEqualTo("Batch_variation_notice_UK_ETS.pdf");
    	assertThat(cut.map(EmissionTradingScheme.CORSIA)).isEqualTo("Batch_variation_notice_CORSIA.pdf");
    	assertThat(cut.map(EmissionTradingScheme.EU_ETS_INSTALLATIONS)).isNull();
    }
}
