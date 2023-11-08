package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmittedRequestActionPayload;

class NonComplianceMapperTest {

    private final NonComplianceMapper nonComplianceMapper = Mappers.getMapper(NonComplianceMapper.class);

    @Test
    void toSubmittedRequestAction() {

        final NonComplianceApplicationSubmitRequestTaskPayload taskPayload =
            NonComplianceApplicationSubmitRequestTaskPayload.builder()
                .availableRequests(List.of(
                        RequestInfoDTO.builder().id("id1").type(RequestType.PERMIT_TRANSFER_A).build(),
                        RequestInfoDTO.builder().id("id2").type(RequestType.PERMIT_ISSUANCE).build(),
                        RequestInfoDTO.builder().id("id3").type(RequestType.PERMIT_REVOCATION).build()
                    )
                )
                .selectedRequests(Set.of("id1", "id2"))
                .build();

        final NonComplianceApplicationSubmittedRequestActionPayload actionPayload =
            nonComplianceMapper.toSubmittedRequestAction(taskPayload);

        assertEquals(actionPayload.getSelectedRequests(), Set.of(
            RequestInfoDTO.builder().id("id1").type(RequestType.PERMIT_TRANSFER_A).build(),
            RequestInfoDTO.builder().id("id2").type(RequestType.PERMIT_ISSUANCE).build()
        ));
    }
}