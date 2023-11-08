package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.transform.LegalEntityMapper;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;


@Mapper(componentModel = "spring", uses = {LocationMapper.class, LegalEntityMapper.class}, config = MapperConfig.class)
public interface InstallationAccountPayloadMapper {

	InstallationAccountDTO toAccountInstallationDTO(InstallationAccountPayload accountPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)")
	InstallationAccountOpeningRequestPayload toInstallationAccountOpeningRequestPayload(InstallationAccountPayload accountPayload, String operatorAssignee);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD)")
    InstallationAccountOpeningApplicationSubmittedRequestActionPayload toInstallationAccountOpeningApplicationSubmittedRequestActionPayload(InstallationAccountPayload accountPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)")
    InstallationAccountOpeningApplicationRequestTaskPayload toInstallationAccountOpeningApplicationRequestTaskPayload(InstallationAccountPayload accountPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD)")
    InstallationAccountOpeningApprovedRequestActionPayload toInstallationAccountOpeningApprovedRequestActionPayload(InstallationAccountPayload accountPayload);
}
