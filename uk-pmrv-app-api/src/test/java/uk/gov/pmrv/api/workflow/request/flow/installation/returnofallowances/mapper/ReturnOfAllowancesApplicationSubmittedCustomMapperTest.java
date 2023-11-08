package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmittedRequestActionPayload;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReturnOfAllowancesApplicationSubmittedCustomMapperTest {

    private final ReturnOfAllowancesApplicationSubmittedCustomMapper mapper =
        new ReturnOfAllowancesApplicationSubmittedCustomMapper();

    @Test
    void toRequestActionDTO() {
        ReturnOfAllowancesApplicationSubmittedRequestActionPayload actionPayload =
            ReturnOfAllowancesApplicationSubmittedRequestActionPayload.builder()
                .returnOfAllowances(ReturnOfAllowances.builder()
                    .reason("reason")
                    .years(List.of(2023, 2024))
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
                .type(RequestType.RETURN_OF_ALLOWANCES)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build())
            .payload(actionPayload).build();
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);
        assertEquals("1", result.getRequestId());
        assertEquals(1L, result.getRequestAccountId());
        assertEquals(RequestType.RETURN_OF_ALLOWANCES, result.getRequestType());
        assertEquals(CompetentAuthorityEnum.ENGLAND, result.getCompetentAuthority());
        ReturnOfAllowancesApplicationSubmittedRequestActionPayload returnOfAllowancesApplicationSubmittedRequestActionPayload =
            (ReturnOfAllowancesApplicationSubmittedRequestActionPayload) result.getPayload();
        assertEquals("reason", returnOfAllowancesApplicationSubmittedRequestActionPayload.getReturnOfAllowances().getReason());
        assertEquals(List.of(2023, 2024), returnOfAllowancesApplicationSubmittedRequestActionPayload.getReturnOfAllowances().getYears());
        assertNull(returnOfAllowancesApplicationSubmittedRequestActionPayload.getReturnOfAllowances().getRegulatorComments());
    }

    @Test
    void getRequestActionType() {
        RequestActionType result = mapper.getRequestActionType();
        assertEquals(RequestActionType.RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED, result);
    }

    @Test
    void getUserRoleTypes() {
        Set<RoleType> result = mapper.getUserRoleTypes();
        assertEquals(Set.of(RoleType.OPERATOR, RoleType.VERIFIER), result);
    }
}
