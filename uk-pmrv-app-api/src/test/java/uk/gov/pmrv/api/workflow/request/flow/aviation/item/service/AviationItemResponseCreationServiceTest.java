package uk.gov.pmrv.api.workflow.request.flow.aviation.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT;

@ExtendWith(MockitoExtension.class)
class AviationItemResponseCreationServiceTest {

    @InjectMocks
    private AviationItemResponseCreationService aviationItemResponseCreationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private EmissionsMonitoringPlanQueryService empQueryService;


    @Test
    void getAccountType() {
        assertEquals(AccountType.AVIATION, aviationItemResponseCreationService.getAccountType());
    }

    @Test
    void toItemDTOResponse_operator_same_assignee() {
        String userId = "operatorUserId";
        RoleType userRoleType = RoleType.OPERATOR;
        Long accountId = 1L;
        PmrvUser operatorUser = buildOperatorUser(userId, "oper1", "oper1", accountId);
        AccountInfoDTO operatorAccountInfo = buildAccountInfo(accountId);
        Item item = buildItem(ACCOUNT_USERS_SETUP, userId, accountId);
        String empId = "empId";

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName("oper1")
                .lastName("oper1")
                .build(),
            userRoleType,
            empId);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(accountQueryService.getAccountsInfoByIds(List.of(accountId))).thenReturn(List.of(operatorAccountInfo));
        when(userRoleTypeService.getUserRoleTypeByUserId(userId)).thenReturn(UserRoleTypeDTO.builder().roleType(userRoleType).build());
        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));

        // Invoke
        ItemDTOResponse actualItemDTOResponse =
            aviationItemResponseCreationService.toItemDTOResponse(itemPage, operatorUser);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(userId);
        verify(userAuthService, never()).getUsers(anyList());
        verify(accountQueryService, times(1)).getAccountsInfoByIds(List.of(accountId));
        verify(empQueryService, times(1)).getEmpIdByAccountId(accountId);
    }

    @Test
    void toItemDTOResponse_operator_different_assignee() {
        Long accountId = 1L;
        PmrvUser operatorUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        AccountInfoDTO operatorAccountInfo = buildAccountInfo(accountId);
        Item item = buildItem(ACCOUNT_USERS_SETUP, "oper2Id", accountId);
        String empId = "empId";

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName("oper2")
                .lastName("oper2")
                .build(),
            RoleType.OPERATOR,
            empId);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(userAuthService.getUsers(List.of(item.getTaskAssigneeId())))
            .thenReturn(List.of(UserInfo.builder().id("oper2Id").firstName("oper2").lastName("oper2").build()));
        when(userRoleTypeService.getUserRoleTypeByUserId("oper2Id"))
            .thenReturn(UserRoleTypeDTO.builder().roleType(RoleType.OPERATOR).build());
        when(accountQueryService.getAccountsInfoByIds(List.of(accountId))).thenReturn(List.of(operatorAccountInfo));
        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));

        // Invoke
        ItemDTOResponse actualItemDTOResponse =
            aviationItemResponseCreationService.toItemDTOResponse(itemPage, operatorUser);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId("oper2Id");
        verify(userAuthService, times(1)).getUsers(List.of(item.getTaskAssigneeId()));
        verify(accountQueryService, times(1)).getAccountsInfoByIds(List.of(accountId));
        verify(empQueryService, times(1)).getEmpIdByAccountId(accountId);
    }

    @Test
    void toItemDTOResponse_operator_only_request() {
        Long accountId = 1L;
        PmrvUser operatorUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        AccountInfoDTO operatorAccountInfo = buildAccountInfo(accountId);
        Item item = Item.builder()
            .creationDate(LocalDateTime.now())
            .requestId("1")
            .requestType(RequestType.EMP_ISSUANCE_UKETS)
            .accountId(accountId)
            .build();
        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemDTO expectedItemDTO = ItemDTO.builder()
            .creationDate(item.getCreationDate())
            .requestId(item.getRequestId())
            .requestType(item.getRequestType())
            .account(ItemAccountDTO.builder()
                .accountId(item.getAccountId())
                .accountName("accountName")
                .competentAuthority(ENGLAND)
                .build())
            .permitReferenceId(null)
            .build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(accountQueryService.getAccountsInfoByIds(List.of(accountId))).thenReturn(List.of(operatorAccountInfo));
        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.empty());

        // Invoke
        ItemDTOResponse actualItemDTOResponse =
            aviationItemResponseCreationService.toItemDTOResponse(itemPage, operatorUser);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, never()).getUserRoleTypeByUserId(anyString());
        verify(userAuthService, never()).getUsers(anyList());
        verify(accountQueryService, times(1)).getAccountsInfoByIds(List.of(accountId));
        verify(empQueryService, times(1)).getEmpIdByAccountId(accountId);
    }

    @Test
    void toItemDTOResponse_regulator_same_assignee() {
        Long accountId = 1L;
        PmrvUser regulatorUser = buildRegulatorUser("reg1Id", "reg1", ENGLAND);
        AccountInfoDTO operatorAccountInfo = buildAccountInfo(accountId);
        Item item = buildItem(EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT, "reg1Id", accountId);
        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName("reg1")
                .lastName("reg1")
                .build(),
            RoleType.REGULATOR,
            null);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(accountQueryService.getAccountsInfoByIds(List.of(item.getAccountId()))).thenReturn(List.of(operatorAccountInfo));
        when(userRoleTypeService.getUserRoleTypeByUserId("reg1Id")).thenReturn(UserRoleTypeDTO.builder().roleType(RoleType.REGULATOR).build());
        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.empty());

        // Invoke
        ItemDTOResponse actualItemDTOResponse =
            aviationItemResponseCreationService.toItemDTOResponse(itemPage, regulatorUser);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId("reg1Id");
        verify(userAuthService, never()).getUsers(anyList());
        verify(accountQueryService, times(1)).getAccountsInfoByIds(List.of(item.getAccountId()));
        verify(empQueryService, times(1)).getEmpIdByAccountId(accountId);
    }

    @Test
    void toItemDTOResponse_regulator_oneItemWithSameAssignee_oneUnassigned() {
        Long accountId = 1L;
        PmrvUser regulatorUser = buildRegulatorUser("reg1Id", "reg1", ENGLAND);
        AccountInfoDTO operatorAccountInfo = buildAccountInfo(accountId);
        Item item1 = buildItem(EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT, "reg1Id", accountId);
        Item item2 = buildItem(EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT, null, accountId);

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item1, item2))
            .totalItems(1L)
            .build();

        ItemDTO expectedItemDTO1 = buildItemDTO(
            item1,
            UserInfoDTO.builder().firstName("reg1").lastName("reg1").build(),
            RoleType.REGULATOR,
            null);
        ItemDTO expectedItemDTO2 = buildItemDTO(
            item2,
            null,
            RoleType.REGULATOR,
            null);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO1, expectedItemDTO2))
            .totalItems(1L)
            .build();

        // Mock
        when(accountQueryService.getAccountsInfoByIds(List.of(item1.getAccountId(), item2.getAccountId())))
            .thenReturn(List.of(operatorAccountInfo));

        when(userRoleTypeService.getUserRoleTypeByUserId("reg1Id"))
            .thenReturn(UserRoleTypeDTO.builder().roleType(RoleType.REGULATOR).build());

        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.empty());

        // Invoke
        ItemDTOResponse actualItemDTOResponse =
            aviationItemResponseCreationService.toItemDTOResponse(itemPage, regulatorUser);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId("reg1Id");
        verify(userAuthService, never()).getUsers(anyList());
        verify(accountQueryService, times(1))
            .getAccountsInfoByIds(List.of(item1.getAccountId(), item2.getAccountId()));
        verify(empQueryService, times(1)).getEmpIdByAccountId(accountId);
    }

    @Test
    void toItemDTOResponse_regulator_different_assignee() {
        Long accountId = 1L;
        PmrvUser regulatorUser = buildRegulatorUser("reg1Id", "reg1", ENGLAND);
        AccountInfoDTO operatorAccountInfo = buildAccountInfo(accountId);
        Item item = buildItem(EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT, "reg2Id", accountId);
        String empId = "empId";

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName("reg2")
                .lastName("reg2")
                .build(),
            RoleType.REGULATOR,
            empId);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(userAuthService.getUsers(List.of(item.getTaskAssigneeId())))
            .thenReturn(List.of(UserInfo.builder().id("reg2Id").firstName("reg2").lastName("reg2").build()));

        when(accountQueryService.getAccountsInfoByIds(List.of(item.getAccountId())))
            .thenReturn(List.of(operatorAccountInfo));

        when(userRoleTypeService.getUserRoleTypeByUserId("reg2Id"))
            .thenReturn(UserRoleTypeDTO.builder().roleType(RoleType.REGULATOR).build());

        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));

        // Invoke
        ItemDTOResponse actualItemDTOResponse =
            aviationItemResponseCreationService.toItemDTOResponse(itemPage, regulatorUser);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId("reg2Id");
        verify(userAuthService, times(1)).getUsers(List.of(item.getTaskAssigneeId()));
        verify(accountQueryService, times(1))
            .getAccountsInfoByIds(List.of(item.getAccountId()));
        verify(empQueryService, times(1)).getEmpIdByAccountId(accountId);
    }

    @Test
    void toItemDTOResponse_verifier_same_assignee() {
        Long vbId = 1L;
        String user = "user";
        String fn = "fn";
        String ln = "ln";
        Long accountId = 1L;
        PmrvUser verifierUser = buildVerifierUser(user, fn, ln, vbId);
        AccountInfoDTO operatorAccountInfo = buildAccountInfo(accountId);
        Item item = buildItem(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT, user, accountId);
        String empId = "empId";

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName(fn)
                .lastName(ln)
                .build(),
            RoleType.VERIFIER,
            empId);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(accountQueryService.getAccountsInfoByIds(List.of(item.getAccountId())))
            .thenReturn(List.of(operatorAccountInfo));

        when(userRoleTypeService.getUserRoleTypeByUserId(user))
            .thenReturn(UserRoleTypeDTO.builder().roleType(RoleType.VERIFIER).build());

        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));

        // Invoke
        ItemDTOResponse actualItemDTOResponse =
            aviationItemResponseCreationService.toItemDTOResponse(itemPage, verifierUser);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userAuthService, never()).getUsers(anyList());
        verify(accountQueryService, times(1)).getAccountsInfoByIds(List.of(item.getAccountId()));
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(user);
        verify(empQueryService, times(1)).getEmpIdByAccountId(accountId);
    }

    @Test
    void toItemDTOResponse_verifier_different_assignee() {
        Long vbId = 1L;
        String user = "user";
        String user_fn = "fn";
        String user_ln = "ln";
        Long accountId = 1L;
        String assignee = "assignee";
        String assignee_fn = "assignee_fn";
        String assignee_ln = "assignee_ln";
        PmrvUser verifierUser = buildVerifierUser(user, user_fn, user_ln, vbId);
        AccountInfoDTO operatorAccountInfo = buildAccountInfo(accountId);
        Item item = buildItem(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT, assignee, accountId);
        String empId = "empId";

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName(assignee_fn)
                .lastName(assignee_ln)
                .build(),
            RoleType.VERIFIER,
            empId);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(userAuthService.getUsers(List.of(item.getTaskAssigneeId())))
            .thenReturn(List.of(UserInfo.builder().id(assignee).firstName(assignee_fn).lastName(assignee_ln).build()));

        when(accountQueryService.getAccountsInfoByIds(List.of(accountId)))
            .thenReturn(List.of(operatorAccountInfo));

        when(userRoleTypeService.getUserRoleTypeByUserId(assignee))
            .thenReturn(UserRoleTypeDTO.builder().roleType(RoleType.VERIFIER).build());

        when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));

        // Invoke
        ItemDTOResponse actualItemDTOResponse =
            aviationItemResponseCreationService.toItemDTOResponse(itemPage, verifierUser);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userAuthService, times(1)).getUsers(List.of(item.getTaskAssigneeId()));
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(assignee);
        verify(accountQueryService, times(1)).getAccountsInfoByIds(List.of(item.getAccountId()));
        verify(empQueryService, times(1)).getEmpIdByAccountId(accountId);
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

    private PmrvUser buildRegulatorUser(String userId, String username, CompetentAuthorityEnum ca) {
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
            .competentAuthority(ca)
            .build();

        return PmrvUser.builder()
            .userId(userId)
            .firstName(username)
            .lastName(username)
            .authorities(List.of(pmrvAuthority))
            .roleType(RoleType.REGULATOR)
            .build();
    }

    private PmrvUser buildVerifierUser(String userId, String fn, String ln, Long vbId) {
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
            .verificationBodyId(vbId)
            .build();

        return PmrvUser.builder()
            .userId(userId)
            .firstName(fn)
            .lastName(ln)
            .authorities(List.of(pmrvAuthority))
            .roleType(RoleType.VERIFIER)
            .build();
    }

    private Item buildItem(RequestTaskType taskType, String assigneeId, Long accountId) {
        return Item.builder()
            .creationDate(LocalDateTime.now())
            .requestId("1")
            .requestType(RequestType.EMP_ISSUANCE_UKETS)
            .taskId(1L)
            .taskType(taskType)
            .taskAssigneeId(assigneeId)
            .taskDueDate(LocalDate.of(2021, 1, 1))
            .accountId(accountId)
            .build();
    }

    private ItemDTO buildItemDTO(Item item, UserInfoDTO taskAssignee, RoleType roleType, String empId) {
        return ItemDTO.builder()
            .creationDate(item.getCreationDate())
            .requestId(item.getRequestId())
            .requestType(item.getRequestType())
            .taskId(item.getTaskId())
            .taskType(item.getTaskType())
            .itemAssignee(taskAssignee != null ?
                ItemAssigneeDTO.builder()
                    .taskAssignee(taskAssignee)
                    .taskAssigneeType(roleType)
                    .build() : null)
            .daysRemaining(DAYS.between(LocalDate.now(), item.getTaskDueDate()))
            .account(ItemAccountDTO.builder()
                .accountId(item.getAccountId())
                .accountName("accountName")
                .competentAuthority(ENGLAND)
                .build())
            .permitReferenceId(empId)
            .build();
    }

    private AccountInfoDTO buildAccountInfo(Long accountId) {
        return AccountInfoDTO.builder()
            .id(accountId)
            .name("accountName")
            .competentAuthority(ENGLAND)
            .build();
    }
}