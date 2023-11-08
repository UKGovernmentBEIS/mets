package uk.gov.pmrv.api.workflow.request.application.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

@ExtendWith(MockitoExtension.class)
class ItemUnassignedRegulatorServiceTest {

    @InjectMocks
    private ItemUnassignedRegulatorService service;
    
    @Mock
    private ItemRegulatorRepository itemRegulatorRepository;

    @Mock
    private ItemResponseService itemResponseService;
    
    @Mock
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;


    @Test
    void getUnassignedItems() {
        final AccountType accountType = AccountType.INSTALLATION;
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        PmrvUser pmrvUser = buildRegulatorUser("reg1");
        Item expectedItem = mock(Item.class);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = mock(ItemDTO.class);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType))
            .thenReturn(scopedRequestTaskTypes);
        when(itemRegulatorRepository.findItems(pmrvUser.getUserId(), ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, accountType, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertThat(actualResponse).isEqualTo(expectedItemDTOResponse);
        
        verify(regulatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType);
    }
    
    @Test
    void getUnassignedItems_empty_scopes() {
        final AccountType accountType = AccountType.INSTALLATION;
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();
        
        PmrvUser pmrvUser = buildRegulatorUser("reg1");

        // Mock
        when(regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType))
            .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        // Assert
        assertThat(actualResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());
        
        verify(regulatorAuthorityResourceAdapter, times(1))
            .getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType);
        verify(itemRegulatorRepository, never()).findItems(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        verify(itemResponseService, never()).toItemDTOResponse(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void getUnassignedItems_ReturnsEmptyResponseWhenNoItemsFetched() {
        final AccountType accountType = AccountType.INSTALLATION;
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        PmrvUser pmrvUser = buildRegulatorUser("reg1");
        ItemPage itemPage = ItemPage.builder()
                .items(Collections.emptyList())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.emptyItemDTOResponse();

        // Mock
        when(regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(pmrvUser.getUserId(), accountType))
            .thenReturn(scopedRequestTaskTypes);
        when(itemRegulatorRepository.findItems(pmrvUser.getUserId(), ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
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
        assertEquals(RoleType.REGULATOR, service.getRoleType());
    }

    private PmrvUser buildRegulatorUser(String userId) {
        return PmrvUser.builder()
                .userId(userId)
                .roleType(RoleType.REGULATOR)
                .build();
    }
}
