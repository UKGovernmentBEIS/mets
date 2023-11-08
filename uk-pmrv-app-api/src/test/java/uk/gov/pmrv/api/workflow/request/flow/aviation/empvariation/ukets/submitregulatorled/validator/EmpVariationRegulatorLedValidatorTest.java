package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.validator;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.validator.EmpVariationUkEtsDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationRegulatorLedValidatorTest {

	@InjectMocks
    private EmpVariationRegulatorLedValidator cut;
	
	@Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empValidatorService;
	
	@Mock
    private EmpVariationUkEtsDetailsValidator empVariationDetailsValidator;
	
	@Test
	void validateEmp() {
		EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
	    			.operatorDetails(EmpOperatorDetails.builder()
	    					.crcoCode("crco1")
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
				.empVariationDetails(EmpVariationUkEtsDetails.builder()
						.reason("reason")
						.build())
				.build();
		
		cut.validateEmp(requestTaskPayload);
		
		verify(empValidatorService, times(1)).validateEmissionsMonitoringPlan(Mappers
				.getMapper(EmpVariationUkEtsMapper.class).toEmissionsMonitoringPlanUkEtsContainer(requestTaskPayload));
		verify(empVariationDetailsValidator, times(1)).validate(requestTaskPayload.getEmpVariationDetails());
	}
}
