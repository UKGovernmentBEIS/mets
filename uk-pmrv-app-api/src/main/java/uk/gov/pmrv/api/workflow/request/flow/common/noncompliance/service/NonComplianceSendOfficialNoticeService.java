package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import java.util.UUID;

import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;

public interface NonComplianceSendOfficialNoticeService {

	void sendOfficialNotice(UUID officialNotice, Request request, NonComplianceDecisionNotification decisionNotification);
	
	AccountType getAccountType();
}
