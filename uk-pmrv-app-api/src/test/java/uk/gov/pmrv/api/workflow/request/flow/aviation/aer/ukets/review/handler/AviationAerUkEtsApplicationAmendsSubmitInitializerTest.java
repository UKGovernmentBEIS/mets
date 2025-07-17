package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private AviationAerUkEtsApplicationAmendsSubmitInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper;

    @Test
    void initializePayload() {
        String requestId = "REQ_ID";
        Long accountId = 1L;
        Year reportingYear = Year.of(2023);
        AviationAerRequestMetadata requestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder().build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(requestPayload)
            .metadata(requestMetadata)
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
        		.serviceContactDetails(ServiceContactDetails.builder().email("email").build())
        		.crcoCode("latest_crcocode")
        		.build();

        when(requestAviationAccountQueryService.getAccountInfo(accountId)).thenReturn(accountInfo);
        
		AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload requestTaskPayload = AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload
				.builder()
				.empOriginatedData(EmpUkEtsOriginatedData.builder()
						.operatorDetails(AviationOperatorDetails.builder().build())
						.build())
				.aer(AviationAerUkEts.builder()
						.operatorDetails(AviationOperatorDetails.builder().build())
						.build())
				.build();
		when(aviationAerUkEtsReviewMapper.toAviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload(requestPayload,
				RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD,
				reportingYear
				)).thenReturn(requestTaskPayload);

        //invoke
        RequestTaskPayload result = initializer.initializePayload(request);
        
        assertThat(result).isEqualTo(AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload
				.builder()
				.serviceContactDetails(accountInfo.getServiceContactDetails())
				.empOriginatedData(EmpUkEtsOriginatedData.builder()
						.operatorDetails(AviationOperatorDetails.builder()
								.crcoCode("latest_crcocode")
								.build())
						.build())
				.aer(AviationAerUkEts.builder()
						.operatorDetails(AviationOperatorDetails.builder()
								.crcoCode("latest_crcocode")
								.build())
						.build())
				.build());

        //verify
        verify(requestAviationAccountQueryService, times(1)).getAccountInfo(accountId);
        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload(
                requestPayload,
                RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD,
                reportingYear
            );
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsOnly(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT);
    }
}