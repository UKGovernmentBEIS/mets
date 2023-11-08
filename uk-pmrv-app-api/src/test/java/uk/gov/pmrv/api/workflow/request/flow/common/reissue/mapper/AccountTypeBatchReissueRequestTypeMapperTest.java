package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

class AccountTypeBatchReissueRequestTypeMapperTest {

	private final AccountTypeBatchReissueRequestTypeMapper cut = Mappers.getMapper(AccountTypeBatchReissueRequestTypeMapper.class);

    @Test
    void accountTypeToBatchReissueRequestType() {
    	RequestType result = cut.accountTypeToBatchReissueRequestType(AccountType.INSTALLATION);
    	assertThat(result).isEqualTo(RequestType.PERMIT_BATCH_REISSUE);
    	
    	result = cut.accountTypeToBatchReissueRequestType(AccountType.AVIATION);
    	assertThat(result).isEqualTo(RequestType.EMP_BATCH_REISSUE);
    }
    
    @Test
    void accountTypeToReissueRequestType() {
    	RequestType result = cut.accountTypeToReissueRequestType(AccountType.INSTALLATION);
    	assertThat(result).isEqualTo(RequestType.PERMIT_REISSUE);
    	
    	result = cut.accountTypeToReissueRequestType(AccountType.AVIATION);
    	assertThat(result).isEqualTo(RequestType.EMP_REISSUE);
    }
    
    @Test
    void accountTypeToBatchReissueSubmittedRequestActionPayloadType() {
    	RequestActionPayloadType result = cut.accountTypeToBatchReissueSubmittedRequestActionPayloadType(AccountType.INSTALLATION);
    	assertThat(result).isEqualTo(RequestActionPayloadType.PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD);
    	
    	result = cut.accountTypeToBatchReissueSubmittedRequestActionPayloadType(AccountType.AVIATION);
    	assertThat(result).isEqualTo(RequestActionPayloadType.EMP_BATCH_REISSUE_SUBMITTED_PAYLOAD);
    }
    
    @Test
    void accountTypeToBatchReissueCompletedRequestActionPayloadType() {
    	RequestActionPayloadType result = cut.accountTypeToBatchReissueCompletedRequestActionPayloadType(AccountType.INSTALLATION);
    	assertThat(result).isEqualTo(RequestActionPayloadType.PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD);
    	
    	result = cut.accountTypeToBatchReissueCompletedRequestActionPayloadType(AccountType.AVIATION);
    	assertThat(result).isEqualTo(RequestActionPayloadType.EMP_BATCH_REISSUE_COMPLETED_PAYLOAD);
    }
    
    @Test
    void accountTypeReissueCompletedRequestActionPayloadType() {
    	RequestActionPayloadType result = cut.accountTypeReissueCompletedRequestActionPayloadType(AccountType.INSTALLATION);
    	assertThat(result).isEqualTo(RequestActionPayloadType.PERMIT_REISSUE_COMPLETED_PAYLOAD);
    	
    	result = cut.accountTypeReissueCompletedRequestActionPayloadType(AccountType.AVIATION);
    	assertThat(result).isEqualTo(RequestActionPayloadType.EMP_REISSUE_COMPLETED_PAYLOAD);
    }
    
}
