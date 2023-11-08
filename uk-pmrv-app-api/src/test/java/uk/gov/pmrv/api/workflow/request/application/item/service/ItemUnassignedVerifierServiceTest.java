package uk.gov.pmrv.api.workflow.request.application.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemVerifierRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

@ExtendWith(MockitoExtension.class)
class ItemUnassignedVerifierServiceTest {

    @InjectMocks
    private ItemUnassignedVerifierService service;

    @Mock
    private ItemVerifierRepository itemRepository;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private VerifierAuthorityResourceAdapter verifierAuthorityResourceService;


    @Test
    void getUnassignedItems() {
        final AccountType accountType = AccountType.INSTALLATION;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();
        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(verifierAuthorityResourceService.getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType))
                .thenReturn(scopedRequestTaskTypes);
        when(itemRepository.findItems(pmrvUser.getUserId(), ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, accountType, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertThat(actualResponse).isEqualTo(expectedItemDTOResponse);

        verify(verifierAuthorityResourceService, times(1))
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);
    }

    @Test
    void getUnassignedItems_empty_scopes() {
        final AccountType accountType = AccountType.INSTALLATION;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();

        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();

        // Mock
        when(verifierAuthorityResourceService.getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType))
                .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertThat(actualResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());

        verify(verifierAuthorityResourceService, times(1))
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);
        verify(itemRepository, never()).findItems(anyString(), Mockito.any(), anyMap(), any(PagingRequest.class));
        verify(itemResponseService, never()).toItemDTOResponse(any(), any(), any());
    }

    @Test
    void getUnassignedItems_ReturnsEmptyResponseWhenNoItemsFetched() {
        final AccountType accountType = AccountType.INSTALLATION;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();
        ItemPage itemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.emptyItemDTOResponse();

        // Mock
        when(verifierAuthorityResourceService.getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType))
                .thenReturn(scopedRequestTaskTypes);
        when(itemRepository.findItems(pmrvUser.getUserId(), ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(itemPage);
        when(itemResponseService.toItemDTOResponse(itemPage, accountType, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertThat(actualResponse).isEqualTo(expectedItemDTOResponse);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.VERIFIER, service.getRoleType());
    }
}
