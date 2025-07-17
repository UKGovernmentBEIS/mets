package uk.gov.pmrv.api.workflow.request.application.item.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface ItemMapper {

    @Mapping(target = "accountId", source = "id")
    @Mapping(target = "accountName", source = "name")
    ItemAccountDTO accountToItemAccountDTO(AccountInfoDTO accountInfoDTO);

    @Mapping(target = "itemAssignee.taskAssignee", source = "taskAssignee")
    @Mapping(target = "itemAssignee.taskAssigneeType", source = "taskAssigneeType")
    @Mapping(target = "daysRemaining", expression = "java(uk.gov.pmrv.api.workflow.utils.DateUtils.getTaskExpirationDaysRemaining(item.getPauseDate(), item.getTaskDueDate()))")
    ItemDTO itemToItemDTO(Item item, UserInfoDTO taskAssignee, String taskAssigneeType, ItemAccountDTO account, String permitReferenceId);
}
