package uk.gov.pmrv.api.workflow.request.application.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.OperatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemOperatorRepository;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToOthersOperatorServiceTest {

    @InjectMocks
    private ItemAssignedToOthersOperatorService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemOperatorRepository itemOperatorRepository;

    @Mock
    private OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;

    @Test
    void getItemsAssignedToOthers() {
        final AccountType accountType = AccountType.INSTALLATION;
        final Long accountId = 1L;
        PmrvUser pmrvUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(accountId, Set.of(ACCOUNT_USERS_SETUP));

        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L)
                .build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L)
                .build();

        // Mock
        doReturn(scopedRequestTaskTypes)
            .when(operatorAuthorityResourceAdapter).getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);
        doReturn(expectedItemPage).when(itemOperatorRepository).findItems(pmrvUser.getUserId(), ItemAssignmentType.OTHERS,
            scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, accountType, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthers(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
    }

    @Test
    void getItemsAssignedToOthers_no_user_authorities() {
        final AccountType accountType = AccountType.INSTALLATION;
        Long accountId = 1L;
        PmrvUser pmrvUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypesAsString = emptyMap();

        // Mock
        doReturn(scopedRequestTaskTypesAsString)
            .when(operatorAuthorityResourceAdapter).getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthers(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verifyNoInteractions(itemOperatorRepository);
        verifyNoInteractions(itemResponseService);

        verify(operatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.OPERATOR, itemService.getRoleType());
    }

    private PmrvUser buildOperatorUser(String userId, String firstName, String lastName, Long accountId) {
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
                .accountId(accountId).build();

        return PmrvUser.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .authorities(List.of(pmrvAuthority))
                .roleType(RoleType.OPERATOR)
                .build();
    }
}
