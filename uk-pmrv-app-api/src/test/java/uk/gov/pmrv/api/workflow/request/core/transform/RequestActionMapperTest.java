package uk.gov.pmrv.api.workflow.request.core.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;

import static org.assertj.core.api.Assertions.assertThat;

class RequestActionMapperTest {

    private RequestActionMapper mapper;
    
    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RequestActionMapper.class);
    }
    
    @Test
    void toRequestActionDTO() {
        Long accountId = 100L;
        Request request = Request.builder()
        		.id("requestId")
        		.accountId(accountId)
        		.competentAuthority(CompetentAuthorityEnum.ENGLAND)
        		.type(RequestType.INSTALLATION_ACCOUNT_OPENING).build();
        RequestActionPayload requestActionPayload = Mockito.mock(RequestActionPayload.class);
        RequestAction requestAction = RequestAction.builder()
            .id(1L)
            .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
            .submitter("fn ln")
            .payload(requestActionPayload)
            .request(request)
            .build();
        
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);
        
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
        assertThat(result.getPayload()).isEqualTo(requestActionPayload);
        assertThat(result.getRequestAccountId()).isEqualTo(accountId);
        assertThat(result.getRequestAccountId()).isEqualTo(accountId);
        assertThat(result.getRequestType()).isEqualTo(RequestType.INSTALLATION_ACCOUNT_OPENING);
        assertThat(result.getCompetentAuthority()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
    }
    
    @Test
    void toRequestActionInfoDTO() {
        RequestAction requestAction = RequestAction.builder()
                .id(1L)
                .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
                .submitter("fn ln")
                .build();
        
        RequestActionInfoDTO result = mapper.toRequestActionInfoDTO(requestAction);
        assertThat(result.getType()).isEqualTo(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
    }
    
    @Test
    void toRequestActionDTOIgnorePayload() {
        Long accountId = 100L;
        Request request = Request.builder()
        		.id("requestId")
        		.accountId(accountId)
        		.competentAuthority(CompetentAuthorityEnum.ENGLAND)
        		.type(RequestType.INSTALLATION_ACCOUNT_OPENING).build();
        RequestAction requestAction = RequestAction.builder()
                .id(1L)
                .payload(PermitSurrenderApplicationRejectedRequestActionPayload.builder().payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD).build())
                .type(RequestActionType.PERMIT_SURRENDER_APPLICATION_REJECTED)
                .submitter("fn ln")
                .request(request)
                .build();
        
        RequestActionDTO result = mapper.toRequestActionDTOIgnorePayload(requestAction);
        assertThat(result.getPayload()).isNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
        assertThat(result.getRequestAccountId()).isEqualTo(accountId);
        assertThat(result.getRequestId()).isEqualTo("requestId");
        assertThat(result.getRequestType()).isEqualTo(RequestType.INSTALLATION_ACCOUNT_OPENING);
        assertThat(result.getCompetentAuthority()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
    }
}
