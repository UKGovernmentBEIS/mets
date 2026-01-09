package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;

public class ReissueChangesDetailsDeserializer extends StdDeserializer<ReissueCompletedRequestActionPayload> {

     public ReissueChangesDetailsDeserializer() {
        super(ReissueCompletedRequestActionPayload.class);
     }

    @Override
    public ReissueCompletedRequestActionPayload deserialize(JsonParser p, DeserializationContext c) throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode node = mapper.readTree(p);

        ReissueCompletedRequestActionPayload payload = ReissueCompletedRequestActionPayload
                .builder()
                .submitter(node.hasNonNull("submitter")? node.get("submitter").asText() : null)
                .signatory(node.hasNonNull("signatory")? node.get("signatory").asText() : null)
                .signatoryName(node.hasNonNull("signatoryName")? node.get("signatoryName").asText() : null)
                .officialNotice(node.hasNonNull("officialNotice")? mapper.treeToValue(node.get("officialNotice"), FileInfoDTO.class) : null)
                .document(node.hasNonNull("document")? mapper.treeToValue(node.get("document"), FileInfoDTO.class) : null)
                .payloadType(node.hasNonNull("payloadType")? mapper.treeToValue(node.get("payloadType"), RequestActionPayloadType.class) : null)
                .build();

        JsonNode changesDetails = node.get("changesDetails");

        if (changesDetails != null) {
            BatchReissueChangesDetails details = switch (payload.getPayloadType()) {
                case RequestActionPayloadType.PERMIT_REISSUE_COMPLETED_PAYLOAD ->
                    mapper.treeToValue(changesDetails, PermitBatchReissueChangesDetails.class);
                case RequestActionPayloadType.EMP_REISSUE_COMPLETED_PAYLOAD ->
                    mapper.treeToValue(changesDetails, EmpBatchReissueChangesDetails.class);
                default -> null;
            };
            payload.setChangesDetails(details);
        } else {
            payload.setChangesDetails(null);
        }

        return payload;
    }

}
