package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

public enum NonComplianceReason {

    FAILURE_TO_SURRENDER_ALLOWANCES_100,
    FAILURE_TO_SURRENDER_ALLOWANCES_20,
    
    // installations
    CARRYING_OUT_A_REGULATED_ACTIVITY_WITHOUT_A_PERMIT,
    FAILURE_TO_MONITOR_REPORTABLE_EMISSIONS,
    FAILURE_TO_REPORT_REPORTABLE_EMISSIONS,
    FAILURE_TO_SUBMIT_AN_IMPROVEMENT_REPORT,
    FAILURE_TO_NOTIFY,
    FAILURE_TO_MONITOR_ACTIVITY_LEVELS,
    FAILURE_TO_REPORT_ACTIVITY_LEVELS,
    FAILURE_TO_COMPLY_WITH_THE_CONDITION_OF_A_PERMIT,
    FAILURE_TO_TRANSFER_OR_SURRENDER_ALLOWANCES_WHEN_UNDERREPORTING_DISCOVERED_AFTER_TRANSFER,
    FAILURE_TO_SURRENDER_A_PERMIT,
    FAILURE_TO_SUBMIT_INFORMATION_UNDER_ARTICLE_27_A,
    
    // hospital or small emitters
    EXCEEDING_EMISSIONS_TARGET,
    FAILURE_TO_PAY_PENALTY_FOR_EXCEEDING_EMISSIONS_TARGET,
    UNDER_REPORTING_EMISSIONS,
    FAILURE_TO_NOTIFY_WHEN_CEASE_TO_MEET_CRITERIA,
    
    // ultra-small emitters
    REPORTABLE_EMISSIONS_EXCEED_MAXIMUM_AMOUNT,
    FAILURE_TO_NOTIFY_WHEN_REPORTABLE_EMISSIONS_EXCEED_MAXIMUM_AMOUNT,
    
    // aviation
    FAILURE_TO_APPLY_FOR_AN_EMISSIONS_MONITORING_PLAN_ETS,
    FAILURE_TO_COMPLY_WITH_A_CONDITION_OF_AN_EMISSIONS_MONITORING_PLAN_ETS,
    FAILURE_TO_MONITOR_AVIATION_EMISSIONS_ETS,
    FAILURE_TO_REPORT_AVIATION_EMISSIONS_ETS,
    FAILURE_TO_APPLY_FOR_AN_EMISSIONS_MONITORING_PLAN_CORSIA,
    FAILURE_TO_COMPLY_WITH_A_CONDITION_OF_AN_EMISSIONS_MONITORING_PLAN_CORSIA,
    FAILURE_TO_MONITOR_EMISSIONS_CORSIA,
    FAILURE_TO_REPORT_EMISSIONS_CORSIA,
    FAILURE_TO_KEEP_RECORDS_CORSIA,

    FAILURE_TO_COMPLY_WITH_DEFICIT_NOTICE,
    FAILURE_TO_COMPLY_WITH_NOTICE_TO_RETURN_ALLOWANCES,
    FAILURE_TO_COMPLY_WITH_AN_ENFORCEMENT_NOTICE_ETS,
    FAILURE_TO_COMPLY_WITH_AN_ENFORCEMENT_NOTICE_CORSIA,
    FAILURE_TO_COMPLY_WITH_AN_INFORMATION_NOTICE_ETS,
    FAILURE_TO_COMPLY_WITH_AN_INFORMATION_NOTICE_CORSIA,
    PROVIDING_FALSE_OR_MISLEADING_INFORMATION_ETS,
    PROVIDING_FALSE_OR_MISLEADING_INFORMATION_CORSIA,
    REFUSAL_TO_ALLOW_ACCESS_TO_PREMISES_ETS,
    REFUSAL_TO_ALLOW_ACCESS_TO_PREMISES_CORSIA,
}