package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturned;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReturnOfAllowancesReturnedApplicationCompletedCustomMapperTest {

    private final ReturnOfAllowancesReturnedApplicationCompletedCustomMapper mapper =
        new ReturnOfAllowancesReturnedApplicationCompletedCustomMapper();

    @Test
    void toRequestActionDTO() {
        ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload actionPayload =
            ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload.builder()
                .returnOfAllowancesReturned(ReturnOfAllowancesReturned.builder()
                    .isAllowancesReturned(Boolean.TRUE)
                    .returnedAllowancesDate(LocalDate.now())
                    .regulatorComments("regulator comments")
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
        ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload returnOfAllowancesReturnedApplicationCompletedRequestActionPayload =
            (ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload) result.getPayload();
        assertNull(returnOfAllowancesReturnedApplicationCompletedRequestActionPayload.getReturnOfAllowancesReturned().getRegulatorComments());
    }

    @Test
    void getRequestActionType() {
        RequestActionType result = mapper.getRequestActionType();
        assertEquals(RequestActionType.RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED, result);
    }

    @Test
    void getUserRoleTypes() {
        Set<RoleType> result = mapper.getUserRoleTypes();
        assertEquals(Set.of(RoleType.OPERATOR, RoleType.VERIFIER), result);
    }

}