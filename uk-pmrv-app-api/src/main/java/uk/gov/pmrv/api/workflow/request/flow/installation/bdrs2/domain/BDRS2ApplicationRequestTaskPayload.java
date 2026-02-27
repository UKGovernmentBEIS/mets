package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BDRS2ApplicationRequestTaskPayload extends RequestTaskPayload {

    private BDRS2 bdrs2;

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> bdrs2SectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> bdrs2Attachments = new HashMap<>();

    private int bdrs2FileVersion;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getBdrs2Attachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Stream.of(
                        super.getReferencedAttachmentIds().stream(),
                        this.getBdrs2Attachments().keySet().stream(),

                        Optional.ofNullable(this.getBdrs2())
                                .map(BDRS2::getBdrs2Files)
                                .stream()
                                .flatMap(files -> Stream.concat(
                                        Stream.ofNullable(files.getFile()),
                                        files.getSupportingFiles() == null
                                                ? Stream.empty()
                                                : files.getSupportingFiles().stream()
                                )),

                        Optional.ofNullable(this.getBdrs2())
                                .map(BDRS2::getMmpFiles)
                                .stream()
                                .flatMap(files -> Stream.concat(
                                        Stream.ofNullable(files.getFile()),
                                        files.getSupportingFiles() == null
                                                ? Stream.empty()
                                                : files.getSupportingFiles().stream()
                                ))
                )
                .flatMap(Function.identity())
                .collect(Collectors.toSet());
    }
}
