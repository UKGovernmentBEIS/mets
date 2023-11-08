package uk.gov.pmrv.api.migration.files;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EtsFileAttachmentType {

    SITE_DIAGRAM("Ia_site_diagram"),
    MONITORING_REPORTING("Man_tree_diagram_or_organisational_chart-Attachments"),
    ADDITIONAL_DOCUMENTS("Ai_additional_information-Attachments"),
    MONITORING_METHODOLOGY_PLAN("Mmp_attach-Attachments"),
    SAMPLING_PLAN("Cf_procedures_analyses_sampling_plan_doc"),
    UNCERTAINTY_ANALYSIS("Calc_uncertainty_calculations_evidence"),
    DATA_FLOW_ACTIVITIES("Man_data_flow_activities_attach-Attachments"),
    RISK_ASSESSMENT("Man_risk_assessment"),
    MMP_SUPPORTING_EVIDENCE("Mmp_supporting_evidence-Attachments"),
    CA_COMMENTS_ON_THE_MMP("Mmp_ca_comments-Attachments"),
    MEASUREMENT_EMISSION_POINT("Meas_emission_point_attach"),
    N2O_EMISSION_POINT("Mn2o_emission_point_attach");

    private String definition;
}
