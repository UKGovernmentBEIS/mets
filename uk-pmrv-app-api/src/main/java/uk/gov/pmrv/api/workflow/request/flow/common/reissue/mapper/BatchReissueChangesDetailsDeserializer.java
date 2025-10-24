package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;

import java.io.IOException;

public class BatchReissueChangesDetailsDeserializer extends StdDeserializer<ReissueCompletedRequestActionPayload> {


     public BatchReissueChangesDetailsDeserializer() {
        super(ReissueCompletedRequestActionPayload.class);
     }


    @Override
    public ReissueCompletedRequestActionPayload  deserialize(JsonParser p, DeserializationContext c) throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode node = mapper.readTree(p);

        ReissueCompletedRequestActionPayload payload = ReissueCompletedRequestActionPayload
                .builder()
                .submitter(node.get("submitter").asText())
                .signatory(node.get("signatory").asText())
                .signatoryName(node.get("signatoryName").asText())
                .officialNotice(mapper.treeToValue(node.get("officialNotice"), FileInfoDTO.class))
                .document(mapper.treeToValue(node.get("document"), FileInfoDTO.class))
                .payloadType(mapper.treeToValue(node.get("payloadType"), RequestActionPayloadType.class))
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
