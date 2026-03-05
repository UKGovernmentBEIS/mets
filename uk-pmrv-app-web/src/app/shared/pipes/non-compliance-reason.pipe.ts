import { Pipe, PipeTransform } from '@angular/core';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Pipe({
  name: 'nonComplianceReason',
})
export class NonComplianceReasonPipe implements PipeTransform {
  transform(value: NonComplianceApplicationSubmitRequestTaskPayload['reason']): string {
    switch (value) {
      case 'FAILURE_TO_SURRENDER_ALLOWANCES_100':
        return 'Failure to surrender allowances (£100/allowance)';
      case 'FAILURE_TO_SURRENDER_ALLOWANCES_20':
        return 'Failure to surrender allowances (£20/allowance)';
      case 'CARRYING_OUT_A_REGULATED_ACTIVITY_WITHOUT_A_PERMIT':
        return 'Installations - carrying out a regulated activity without a permit';
      case 'FAILURE_TO_MONITOR_REPORTABLE_EMISSIONS':
        return 'Installations – failure to monitor reportable emissions';
      case 'FAILURE_TO_REPORT_REPORTABLE_EMISSIONS':
        return 'Installations – failure to report reportable emissions';
      case 'FAILURE_TO_SUBMIT_AN_IMPROVEMENT_REPORT':
        return 'Installations – failure to submit an improvement report';
      case 'FAILURE_TO_NOTIFY':
        return 'Installations – failure to notify';
      case 'FAILURE_TO_MONITOR_ACTIVITY_LEVELS':
        return 'Installations – failure to monitor activity levels';
      case 'FAILURE_TO_REPORT_ACTIVITY_LEVELS':
        return 'Installations – failure to report activity levels';
      case 'FAILURE_TO_COMPLY_WITH_THE_CONDITION_OF_A_PERMIT':
        return 'Installations - failure to comply with a condition of a permit or surrender / revocation notice (other)';
      case 'FAILURE_TO_TRANSFER_OR_SURRENDER_ALLOWANCES_WHEN_UNDERREPORTING_DISCOVERED_AFTER_TRANSFER':
        return 'Installations - failure to transfer or surrender allowances when underreporting discovered after transfer';
      case 'FAILURE_TO_SURRENDER_A_PERMIT':
        return 'Installations - failure to surrender a permit';
      case 'FAILURE_TO_SUBMIT_INFORMATION_UNDER_ARTICLE_27_A':
        return 'Installations - failure to submit information under article 27A';
      case 'EXCEEDING_EMISSIONS_TARGET':
        return 'Hospital or small emitters - exceeding emissions target';
      case 'FAILURE_TO_PAY_PENALTY_FOR_EXCEEDING_EMISSIONS_TARGET':
        return 'Hospital or small emitters - failure to pay penalty for exceeding emissions target';
      case 'UNDER_REPORTING_EMISSIONS':
        return 'Hospital or small emitters - under-reporting emissions';
      case 'FAILURE_TO_NOTIFY_WHEN_CEASE_TO_MEET_CRITERIA':
        return 'Hospital or small emitters - failure to notify when cease to meet criteria';
      case 'REPORTABLE_EMISSIONS_EXCEED_MAXIMUM_AMOUNT':
        return 'Ultra-small emitters - reportable emissions exceed maximum amount';
      case 'FAILURE_TO_NOTIFY_WHEN_REPORTABLE_EMISSIONS_EXCEED_MAXIMUM_AMOUNT':
        return 'Ultra-small emitters – failure to notify when reportable emissions exceed maximum amount';
      case 'FAILURE_TO_APPLY_FOR_AN_EMISSIONS_MONITORING_PLAN_ETS':
        return 'Aviation – failure to apply for an emissions monitoring plan (ETS)';
      case 'FAILURE_TO_COMPLY_WITH_A_CONDITION_OF_AN_EMISSIONS_MONITORING_PLAN_ETS':
        return 'Aviation – failure to comply with a condition of an emissions monitoring plan (ETS)';
      case 'FAILURE_TO_MONITOR_AVIATION_EMISSIONS_ETS':
        return 'Aviation – failure to monitor aviation emissions (ETS)';
      case 'FAILURE_TO_REPORT_AVIATION_EMISSIONS_ETS':
        return 'Aviation – failure to report aviation emissions (ETS)';
      case 'FAILURE_TO_APPLY_FOR_AN_EMISSIONS_MONITORING_PLAN_CORSIA':
        return 'Aviation – failure to apply for an emissions monitoring plan (CORSIA)';
      case 'FAILURE_TO_COMPLY_WITH_A_CONDITION_OF_AN_EMISSIONS_MONITORING_PLAN_CORSIA':
        return 'Aviation – failure to comply with a condition of an emissions monitoring plan (CORSIA)';
      case 'FAILURE_TO_MONITOR_EMISSIONS_CORSIA':
        return 'Aviation – failure to monitor emissions (CORSIA)';
      case 'FAILURE_TO_REPORT_EMISSIONS_CORSIA':
        return 'Aviation – failure to report emissions (CORSIA)';
      case 'FAILURE_TO_KEEP_RECORDS_CORSIA':
        return 'Aviation – failure to keep records (CORSIA)';
      case 'FAILURE_TO_COMPLY_WITH_DEFICIT_NOTICE':
        return 'Failure to comply with deficit notice';
      case 'FAILURE_TO_COMPLY_WITH_NOTICE_TO_RETURN_ALLOWANCES':
        return 'Failure to comply with notice to return allowances';
      case 'FAILURE_TO_COMPLY_WITH_AN_ENFORCEMENT_NOTICE_ETS':
        return 'Failure to comply with an enforcement notice (ETS)';
      case 'FAILURE_TO_COMPLY_WITH_AN_ENFORCEMENT_NOTICE_CORSIA':
        return 'Failure to comply with an enforcement notice (CORSIA)';
      case 'FAILURE_TO_COMPLY_WITH_AN_INFORMATION_NOTICE_ETS':
        return 'Failure to comply with an information notice (ETS)';
      case 'FAILURE_TO_COMPLY_WITH_AN_INFORMATION_NOTICE_CORSIA':
        return 'Failure to comply with an information notice (CORSIA)';
      case 'PROVIDING_FALSE_OR_MISLEADING_INFORMATION_ETS':
        return 'Providing false or misleading information (ETS)';
      case 'PROVIDING_FALSE_OR_MISLEADING_INFORMATION_CORSIA':
        return 'Providing false or misleading information (CORSIA)';
      case 'REFUSAL_TO_ALLOW_ACCESS_TO_PREMISES_ETS':
        return 'Refusal to allow access to premises (ETS)';
      case 'REFUSAL_TO_ALLOW_ACCESS_TO_PREMISES_CORSIA':
        return 'Refusal to allow access to premises (CORSIA)';
      default:
        return '';
    }
  }
}
