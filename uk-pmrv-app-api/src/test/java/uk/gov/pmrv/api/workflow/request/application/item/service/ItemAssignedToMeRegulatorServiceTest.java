package uk.gov.pmrv.api.workflow.request.application.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeRegulatorServiceTest {

    @InjectMocks
    private ItemAssignedToMeRegulatorService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemRegulatorRepository itemRegulatorRepository;

    @Mock
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    @Test
    void getItemsAssignedToMe() {
        final AccountType accountType = AccountType.INSTALLATION;
        PmrvUser pmrvUser = buildRegulatorUser("reg1Id", "reg1");
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(regulatorAuthorityResourceAdapter
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType))
            .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRegulatorRepository).findItems(pmrvUser.getUserId(), ItemAssignmentType.ME,
            scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, accountType, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMe(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(regulatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType);
        verify(itemRegulatorRepository, times(1)).findItems(pmrvUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, accountType, pmrvUser);
    }

    @Test
    void getItemsAssignedToMe_no_user_authorities() {
        final AccountType accountType = AccountType.INSTALLATION;
        PmrvUser pmrvUser = buildRegulatorUser("reg1Id", "reg1");
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes = emptyMap();
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of())
                .totalItems(0L).build();

        // Mock
        doReturn(scopedRequestTaskTypes)
            .when(regulatorAuthorityResourceAdapter)
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType);
        doReturn(expectedItemPage).when(itemRegulatorRepository).findItems(pmrvUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, accountType, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
            .getItemsAssignedToMe(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verify(regulatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType);
        verify(itemRegulatorRepository, times(1))
                .findItems(pmrvUser.getUserId(), ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, accountType, pmrvUser);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.REGULATOR, itemService.getRoleType());
    }

    private PmrvUser buildRegulatorUser(String userId, String username) {
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
                .competentAuthority(ENGLAND)
                .build();

        return PmrvUser.builder()
                .userId(userId)
                .firstName(username)
                .lastName(username)
                .authorities(List.of(pmrvAuthority))
                .roleType(RoleType.REGULATOR)
                .build();
    }
}
