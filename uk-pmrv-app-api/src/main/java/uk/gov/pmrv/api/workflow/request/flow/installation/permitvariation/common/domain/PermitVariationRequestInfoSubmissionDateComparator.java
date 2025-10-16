package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain;

import java.util.Comparator;

public class PermitVariationRequestInfoSubmissionDateComparator implements Comparator<PermitVariationRequestInfo> {

    @Override
    public int compare(PermitVariationRequestInfo infoA, PermitVariationRequestInfo infoB) {
       return infoA.getSubmissionDate().compareTo(infoB.getSubmissionDate());
    }

}