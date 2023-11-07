import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'waReasonDescription',
})
export class WaReasonDescriptionPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'ASSESSING_A_RENUNCIATION_NOTICE':
        return 'Assessing a renunciation notice';
      case 'ASSESSING_A_SPLIT_OR_A_MERGER':
        return 'Assessing a split or a merger';
      case 'ASSESSING_TRANSFER_OF_AVIATION_FREE_ALLOCATION_UNDER_ARTICLE_34Q':
        return 'Assessing transfer of aviation free allocation under article 34Q';
      case 'DETERMINING_APPLICATION_IN_RESPECT_OF_THE_COVID_YEAR':
        return 'Determining application in respect of the Covid year';
      case 'DETERMINING_A_SURRENDER_APPLICATION':
        return 'Determining a surrender application';
      case 'INVESTIGATING_AN_ERROR_IN_AVIATION_ALLOCATION_TABLE':
        return 'Investigating an error in aviation allocation table';
      case 'INVESTIGATING_AN_ERROR_IN_ALLOCATION_TABLE':
        return 'Investigating an error in allocation table';
      case 'INVESTIGATING_IF_PERSON_HAS_PERMANENTLY_CEASED_AN_AVIATION_ACTIVITY':
        return 'Investigating if person has permanently ceased an aviation activity';
      case 'INVESTIGATING_WHETHER_AN_INSTALLATION_HAS_CEASED_OPERATION':
        return 'Investigating whether an installation has ceased operation';
      case 'MONITORING_METHODOLOGY_PLAN_HAS_NOT_BEEN_APPROVED':
        return 'Monitoring methodology plan has not been approved';
      case 'SURRENDER_OR_REVOCATION_NOTICE_HAS_NOT_YET_COME_INTO_EFFECT':
        return 'Surrender or revocation notice has not yet come into effect';
      case 'OTHER':
        return 'Other (please specify)';
      default:
        return value;
    }
  }
}
