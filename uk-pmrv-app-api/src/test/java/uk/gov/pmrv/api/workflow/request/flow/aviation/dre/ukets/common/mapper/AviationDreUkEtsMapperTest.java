package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AviationDreUkEtsMapperTest {

    private final AviationDreUkEtsMapper mapper = Mappers.getMapper(AviationDreUkEtsMapper.class);

    @Test
    void toSubmittedActionPayload() {
        UUID attachment1 = UUID.randomUUID();
        AviationDreUkEtsRequestPayload requestPayload = AviationDreUkEtsRequestPayload.builder()
            .dre(AviationDre.builder()
                .determinationReason(AviationDreDeterminationReason.builder()
                    .furtherDetails("details")
                    .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                    .build())
                .build())
            .sectionCompleted(true)
            .dreAttachments(Map.of(attachment1, "att1.pdf"))
            .payloadType(RequestPayloadType.AVIATION_DRE_UKETS_REQUEST_PAYLOAD)
            .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operatorUserId", RequestActionUserInfo.builder().name("operatorUserName").roleCode("admin").build(),
                "regulatorUserId", RequestActionUserInfo.builder().name("regulatorUserName").roleCode("admin").build()
        );
        AviationDreApplicationSubmittedRequestActionPayload actionPayload = mapper.toSubmittedActionPayload(requestPayload, usersInfo);

        assertThat(actionPayload.getPayloadType()).isEqualTo(RequestActionPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED_PAYLOAD);
        assertThat(actionPayload.getDre()).isEqualTo(requestPayload.getDre());
    }

    @Test
    void cloneSubmittedRequestActionPayloadIgnoreFurtherDetailsFeeComments() {
        UUID attachment1 = UUID.randomUUID();
        AviationDreApplicationSubmittedRequestActionPayload actionPayload = AviationDreApplicationSubmittedRequestActionPayload.builder()
            .dre(AviationDre.builder()
                .determinationReason(AviationDreDeterminationReason.builder()
                    .furtherDetails("details")
                    .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                    .build())
                .fee(AviationDreFee.builder()
                    .feeDetails(AviationDreFeeDetails.builder()
                        .comments("comment")
                        .build())
                    .build())
                .build())
            .sectionCompleted(true)
            .dreAttachments(Map.of(attachment1, "att1.pdf"))
            .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("filename").build())
            .payloadType(RequestActionPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED_PAYLOAD)
            .build();

        AviationDreApplicationSubmittedRequestActionPayload cloned =
            mapper.cloneSubmittedRequestActionPayloadIgnoreFurtherDetailsFeeComments(actionPayload);

        assertThat(cloned.getDre().getFee().getFeeDetails().getComments()).isNull();
        assertThat(cloned.getDre().getDeterminationReason().getFurtherDetails()).isNull();
    }

    @Test
    void toDreApplicationSubmitRequestTaskPayload() {
        AviationDre dre = AviationDre.builder()
            .determinationReason(AviationDreDeterminationReason.builder()
                .furtherDetails("furtherDetials")
                .type(AviationDreDeterminationReasonType.IMPOSING_OR_CONSIDERING_IMPOSING_CIVIL_PENALTY_IN_ACCORDANCE_WITH_ORDER)
                .build())
            .build();

        UUID attachment1 = UUID.randomUUID();
        Map<UUID, String> attachments = new HashMap<>();
        attachments.put(attachment1, "att1");

        boolean sectionsCompleted = true;

        AviationDreUkEtsRequestPayload requestPayload = AviationDreUkEtsRequestPayload.builder()
            .dre(dre)
            .dreAttachments(attachments)
            .sectionCompleted(sectionsCompleted)
            .payloadType(RequestPayloadType.AVIATION_DRE_UKETS_REQUEST_PAYLOAD)
            .build();

        AviationDreUkEtsApplicationSubmitRequestTaskPayload
            result = mapper.toAviationDreUkEtsApplicationSubmitRequestTaskPayload
            (requestPayload, RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(result.getDreAttachments()).containsExactlyEntriesOf(attachments);
        assertThat(result.getDre()).isEqualTo(dre);
        assertThat(result.getSectionCompleted()).isEqualTo(sectionsCompleted);
    }
}
