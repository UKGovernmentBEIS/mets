package uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmittedRequestActionPayload;


@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface ALRMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "alrAttachments", ignore = true)
    ALRApplicationSubmittedRequestActionPayload toALRApplicationSubmittedRequestActionPayload(ALRApplicationSubmitRequestTaskPayload taskPayload,
                                                                                              RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.ALR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "alrSectionsCompleted", ignore = true)
    ALRApplicationVerificationSubmitRequestTaskPayload toALRApplicationVerificationRequestTaskPayload(
            ALRRequestPayload requestPayload,
            InstallationOperatorDetails installationOperatorDetails,
            ALRVerificationReport verificationReport);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestActionPayloadType.ALR_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    ALRApplicationVerificationSubmittedRequestActionPayload toALRApplicationVerificationSubmittedRequestActionPayload(ALRApplicationVerificationSubmitRequestTaskPayload taskPayload);
}
