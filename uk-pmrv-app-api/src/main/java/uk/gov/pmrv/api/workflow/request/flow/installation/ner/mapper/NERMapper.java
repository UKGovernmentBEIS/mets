package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NERMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "installationOperatorDetails", ignore = true)
    @Mapping(target = "nerAttachments", ignore = true)
    NERApplicationSubmittedRequestActionPayload toNERApplicationSubmittedRequestActionPayload(
            NerApplicationSubmitRequestTaskPayload taskPayload,
            RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration" +
            ".RequestTaskPayloadType.NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)")
    @Mapping(target = "verificationReport", source = "verificationReport")
    @Mapping(target = "nerSectionsCompleted", ignore = true)
    NERApplicationVerificationSubmitRequestTaskPayload toNERApplicationVerificationRequestTaskPayload(
            NerRequestPayload requestPayload,
            InstallationOperatorDetails installationOperatorDetails,
            NERVerificationReport verificationReport);
}
