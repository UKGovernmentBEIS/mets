package uk.gov.pmrv.api.workflow.request.application.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemByRequestRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

@ExtendWith(MockitoExtension.class)
class ItemRegulatorServiceTest {

    @InjectMocks
    private ItemRegulatorService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemByRequestRegulatorRepository itemByRequestRegulatorRepository;

    @Mock
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    @Mock
    private RequestService requestService;

    @Test
    void getItemsByRequest() {
        final String requestId = "1";
        final Long accountId = 1L;
        final Request request = Request.builder().id(requestId).accountId(accountId).type(RequestType.PERMIT_ISSUANCE).build();
        final AccountType accountType = AccountType.INSTALLATION;
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        AppUser appUser = buildRegulatorUser("reg1Id");
        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(appUser.getUserId(), accountType))
                .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemByRequestRegulatorRepository).findItemsByRequestId(scopedRequestTaskTypes, requestId);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, accountType, appUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsByRequest(appUser, requestId);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(regulatorAuthorityResourceAdapter, times(1))
                .getUserScopedRequestTaskTypesByAccountType(appUser.getUserId(), accountType);
        verify(itemByRequestRegulatorRepository, times(1))
                .findItemsByRequestId(scopedRequestTaskTypes, requestId);
        verify(itemResponseService, times(1))
                .toItemDTOResponse(expectedItemPage, accountType, appUser);
    }

    @Test
    void getItemsByRequest_empty_scopes() {
        final String requestId = "1";
        final Long accountId = 2L;
        final Request request = Request.builder().id(requestId).accountId(accountId).type(RequestType.PERMIT_ISSUANCE).build();
        final AccountType accountType = AccountType.INSTALLATION;
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();
        AppUser appUser = buildRegulatorUser("reg1Id");

        // Mock
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(appUser.getUserId(), accountType))
                .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsByRequest(appUser, requestId);

        // Assert
        assertThat(actualItemDTOResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());

        verify(requestService, times(1)).findRequestById(requestId);
        verify(regulatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypesByAccountType(appUser.getUserId(), accountType);
        verify(itemByRequestRegulatorRepository, never()).findItemsByRequestId(Mockito.anyMap(), Mockito.anyString());
        verify(itemResponseService, never()).toItemDTOResponse(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void getRoleType() {
        assertEquals(RoleTypeConstants.REGULATOR, itemService.getRoleType());
    }

    private AppUser buildRegulatorUser(String userId) {
        return AppUser.builder()
                .userId(userId)
                .roleType(RoleTypeConstants.REGULATOR)
                .build();
    }
}
