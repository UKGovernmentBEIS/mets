package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmittedRequestActionPayload;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WithholdingOfAllowancesApplicationSubmittedCustomMapperTest {

    private final WithholdingOfAllowancesApplicationSubmittedCustomMapper mapper =
        new WithholdingOfAllowancesApplicationSubmittedCustomMapper();

    @Test
    void toRequestActionDTO() {
        WithholdingOfAllowancesApplicationSubmittedRequestActionPayload actionPayload =
            WithholdingOfAllowancesApplicationSubmittedRequestActionPayload.builder()
                .withholdingOfAllowances(WithholdingOfAllowances.builder()
                    .otherReason("other reason")
                    .year(2023)
                    .regulatorComments("regulator comments")
                    .build())
                .officialNotice(FileInfoDTO.builder()
                    .uuid(UUID.randomUUID().toString())
                    .name("random")
                    .build())
                .build();

        RequestAction requestAction = RequestAction.builder()
            .request(Request.builder()
                .id("1")
                .accountId(1L)
                .type(RequestType.WITHHOLDING_OF_ALLOWANCES)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build())
            .payload(actionPayload).build();
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);
        assertEquals("1", result.getRequestId());
        assertEquals(1L, result.getRequestAccountId());
        assertEquals(RequestType.WITHHOLDING_OF_ALLOWANCES, result.getRequestType());
        assertEquals(CompetentAuthorityEnum.ENGLAND, result.getCompetentAuthority());
        WithholdingOfAllowancesApplicationSubmittedRequestActionPayload withholdingOfAllowancesApplicationSubmittedRequestActionPayload =
            (WithholdingOfAllowancesApplicationSubmittedRequestActionPayload) result.getPayload();
        assertEquals("other reason", withholdingOfAllowancesApplicationSubmittedRequestActionPayload.getWithholdingOfAllowances().getOtherReason());
        assertEquals(2023, withholdingOfAllowancesApplicationSubmittedRequestActionPayload.getWithholdingOfAllowances().getYear());
        assertNull(withholdingOfAllowancesApplicationSubmittedRequestActionPayload.getWithholdingOfAllowances().getRegulatorComments());
    }

    @Test
    void getRequestActionType() {
        RequestActionType result = mapper.getRequestActionType();
        assertEquals(RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED, result);
    }

    @Test
    void getUserRoleTypes() {
        Set<RoleType> result = mapper.getUserRoleTypes();
        assertEquals(Set.of(RoleType.OPERATOR, RoleType.VERIFIER), result);
    }
}
