package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationScope;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PermanentCessationSubmittedCustomMapperTest {

    @InjectMocks
    private PermanentCessationSubmittedCustomMapper submittedCustomMapper;


    @Test
    void ignorePermanentCessationRegulatorComments() {
        PermanentCessation permanentCessation = PermanentCessation.builder()
                .cessationScope(PermanentCessationScope.SUB_INSTALLATIONS_ONLY)
                .regulatorComments("Test comments").build();
        RequestActionPayload requestActionPayload = PermanentCessationApplicationSubmittedRequestActionPayload
                .builder().permanentCessation(permanentCessation)
                .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("Name").build())
                .build();

        RequestAction requestAction = RequestAction.builder().payload(requestActionPayload).build();

        RequestActionDTO requestActionDTO = submittedCustomMapper.toRequestActionDTO(requestAction);
        PermanentCessationApplicationSubmittedRequestActionPayload payload =
                (PermanentCessationApplicationSubmittedRequestActionPayload)requestActionDTO.getPayload();

        assertNotNull(payload.getPermanentCessation());
        assertNull(payload.getPermanentCessation().getRegulatorComments());
    }

    @Test
    void getRequestActionType() {
        RequestActionType requestActionType = submittedCustomMapper.getRequestActionType();
        assertEquals(requestActionType, RequestActionType.PERMANENT_CESSATION_APPLICATION_SUBMITTED);
    }
}