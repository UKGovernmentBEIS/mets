package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

class PermitVariationReviewMapperTest {

    private final PermitVariationReviewMapper mapper = Mappers.getMapper(PermitVariationReviewMapper.class);

    @Test
    void toPermitVariationApplicationReviewRequestTaskPayload() {

        final UUID attachment1 = UUID.randomUUID();
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .abbreviations(Abbreviations.builder().exist(true).build())
                .build())
            .permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
            .permitVariationDetailsCompleted(Boolean.TRUE)
            .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
            .permitAttachments(Map.of(attachment1, "att1"))
            .reviewSectionsCompleted(Map.of("section2", true))
            .build();

        final PermitVariationApplicationReviewRequestTaskPayload result =
            mapper.toPermitVariationApplicationReviewRequestTaskPayload(requestPayload, RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD);

        assertThat(result).isEqualTo(PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .abbreviations(Abbreviations.builder().exist(true).build())
                .build())
            .permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
            .permitVariationDetailsCompleted(Boolean.TRUE)
            .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
            .permitAttachments(Map.of(attachment1, "att1"))
            .reviewSectionsCompleted(Map.of("section2", true))
            .build());
    }
    
    @Test
    void toPermitContainer_fromPermitVariationApplicationReviewRequestTaskPayload() {

        final UUID attachment1 = UUID.randomUUID();
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            PermitVariationApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                .permitType(PermitType.GHGE)
                .permit(Permit.builder()
                    .abbreviations(Abbreviations.builder().exist(true).build())
                    .build())
                .installationOperatorDetails(
                    InstallationOperatorDetails.builder().installationName("installationName1").build())
                .permitAttachments(Map.of(attachment1, "att1"))
                .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
                .reviewSectionsCompleted(Map.of("section1", true))
                .build();

        final PermitContainer result = mapper.toPermitContainer(taskPayload);

        assertThat(result).isEqualTo(PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .abbreviations(Abbreviations.builder().exist(true).build())
                .build())
            .installationOperatorDetails(
                InstallationOperatorDetails.builder().installationName("installationName1").build())
            .permitAttachments(Map.of(attachment1, "att1"))
            .build());
    }
    
    

}
