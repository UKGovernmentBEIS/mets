package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementFallback;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirCreateImprovementDataService;

@ExtendWith(MockitoExtension.class)
class AirCreateActionHandlerTest {

    @InjectMocks
    private AirCreateActionHandler cut;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private DateService dateService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private AirCreateImprovementDataService dataService;

    @Test
    void process() {

        final Permit permit = Permit.builder().build();
        final PermitContainer permitContainer = PermitContainer.builder().permit(permit).build();
        final Long accountId = 1L;
        final List<AirImprovement> airImprovements = List.of(
            AirImprovementCalculationCO2.builder().sourceStreamReference("ref 1").build(),
            AirImprovementFallback.builder().sourceStreamReference("ref 2").build());
        final RequestCreateActionType type = RequestCreateActionType.AIR;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        final RequestParams requestParams = RequestParams.builder()
            .type(RequestType.AIR)
            .accountId(accountId)
            .requestPayload(AirRequestPayload.builder()
                .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                .airImprovements(Map.of(1, AirImprovementCalculationCO2.builder().sourceStreamReference("ref 1").build(),
                    2, AirImprovementFallback.builder().sourceStreamReference("ref 2").build()))
                .build())
            .requestMetadata(AirRequestMetadata.builder()
                .type(RequestMetadataType.AIR)
                .year(Year.of(2022))
                .build())
            .build();

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(dataService.createImprovementData(permit)).thenReturn(airImprovements);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id("reqId").build());
        when(dateService.getYear()).thenReturn(Year.of(2022));

        final String result = cut.process(accountId, type, payload, pmrvUser);

        assertThat(result).isEqualTo("reqId");

        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getType() {
        assertThat(cut.getType()).isEqualTo(RequestCreateActionType.AIR);
    }
}
