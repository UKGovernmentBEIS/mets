package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NERApplicationRequestTaskPayload extends RequestTaskPayload {

    private NER ner;

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> nerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> nerAttachments = new HashMap<>();

    @Builder.Default
    private int nerFileVersion = 1;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNerAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Stream.of(
                        super.getReferencedAttachmentIds().stream(),
                        this.getNerAttachments().keySet().stream(),

                        Optional.ofNullable(this.getNer())
                                .map(NER::getNerFiles)
                                .stream()
                                .flatMap(files -> Stream.concat(
                                        Stream.ofNullable(files.getFile()),
                                        files.getSupportingFiles() == null
                                                ? Stream.empty()
                                                : files.getSupportingFiles().stream()
                                )),

                        Optional.ofNullable(this.getNer())
                                .map(NER::getMmpFiles)
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
