package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.validator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.validator.EmpVariationCorsiaDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRegulatorLedValidatorTest {

	@InjectMocks
    private EmpVariationCorsiaRegulatorLedValidator cut;
	
	@Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empValidatorService;
	
	@Mock
    private EmpVariationCorsiaDetailsValidator empVariationDetailsValidator;
	
	@Test
	void validateEmp() {
		
		EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = 
			EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
	    			.operatorDetails(EmpCorsiaOperatorDetails.builder()
	    					.operatorName("name1")
	    					.build())
	    			.abbreviations(EmpAbbreviations.builder()
	    					.exist(true)
	    					.build())
	    			.additionalDocuments(EmpAdditionalDocuments.builder()
	    					.exist(true)
	    					.build())
	    			.build())
				.serviceContactDetails(ServiceContactDetails.builder()
						.email("email")
						.build())
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("reason")
						.build())
				.build();
		
		cut.validateEmp(requestTaskPayload);
		
		verify(empValidatorService, times(1)).validateEmissionsMonitoringPlan(Mappers
				.getMapper(EmpVariationCorsiaMapper.class).toEmissionsMonitoringPlanCorsiaContainer(requestTaskPayload));
		verify(empVariationDetailsValidator, times(1)).validate(requestTaskPayload.getEmpVariationDetails());
	}
}
