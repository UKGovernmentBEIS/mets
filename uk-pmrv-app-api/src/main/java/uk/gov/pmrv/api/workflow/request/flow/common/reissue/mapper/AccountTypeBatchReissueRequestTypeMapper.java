package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountTypeBatchReissueRequestTypeMapper {

	 @ValueMapping(target = "PERMIT_BATCH_REISSUE", source = "INSTALLATION")
	 @ValueMapping(target = "EMP_BATCH_REISSUE", source = "AVIATION")
	 RequestType accountTypeToBatchReissueRequestType(AccountType accountType);
	 
	 @ValueMapping(target = "PERMIT_REISSUE", source = "INSTALLATION")
	 @ValueMapping(target = "EMP_REISSUE", source = "AVIATION")
	 RequestType accountTypeToReissueRequestType(AccountType accountType);
	 
	 @ValueMapping(target = "PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD", source = "INSTALLATION")
	 @ValueMapping(target = "EMP_BATCH_REISSUE_SUBMITTED_PAYLOAD", source = "AVIATION")
	 RequestActionPayloadType accountTypeToBatchReissueSubmittedRequestActionPayloadType(AccountType accountType);
	 
	 @ValueMapping(target = "PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD", source = "INSTALLATION")
	 @ValueMapping(target = "EMP_BATCH_REISSUE_COMPLETED_PAYLOAD", source = "AVIATION")
	 RequestActionPayloadType accountTypeToBatchReissueCompletedRequestActionPayloadType(AccountType accountType);
	 
	 @ValueMapping(target = "PERMIT_REISSUE_COMPLETED_PAYLOAD", source = "INSTALLATION")
	 @ValueMapping(target = "EMP_REISSUE_COMPLETED_PAYLOAD", source = "AVIATION")
	 RequestActionPayloadType accountTypeReissueCompletedRequestActionPayloadType(AccountType accountType);
	 
}
