package uk.gov.pmrv.api.workflow.request.application.item.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemPage {

    List<Item> items;

    Long totalItems;

}
