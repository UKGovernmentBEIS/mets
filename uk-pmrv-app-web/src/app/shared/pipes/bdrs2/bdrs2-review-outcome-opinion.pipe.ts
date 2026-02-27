import { Pipe, PipeTransform } from '@angular/core';

interface Outcome {
  opinion:
    | 'SENT_TO_AUTHORITY'
    | 'WITHDRAWN'
    | 'NO_ADJUSTMENTS'
    | 'IN_SCOPE_OF_CBAM'
    | 'CBAM_DOES_NOT_APPLY'
    | 'NO_SPLIT_APPLICABLE';
  question: 'fa' | 'covid' | 'installation' | 'cbam';
}

@Pipe({
  name: 'reviewOutcomeOpinion',
})
export class Bdrs2ReviewOutcomeOpinionPipe implements PipeTransform {
  transform(value: Outcome['opinion'], question: Outcome['question']): string {
    switch (value) {
      case 'SENT_TO_AUTHORITY':
        return {
          fa: 'Regulator has sent the free allocation application and explanations to UK ETS authority for assessment',
          covid: 'Regulator has sent COVID adjustments to the UK ETS authority for final assessment',
          installation: 'Installation sector is in scope of CBAM',
          cbam: 'Regulator has sent CBAM classifications to the UK ETS authority for final assessment',
        }[question];
      case 'WITHDRAWN':
        return 'Withdrawn';
      case 'NO_ADJUSTMENTS':
        return 'No adjustments required';
      case 'IN_SCOPE_OF_CBAM':
        return 'In scope of CBAM';
      case 'CBAM_DOES_NOT_APPLY':
        return 'CBAM does not apply';
      case 'NO_SPLIT_APPLICABLE':
        return 'No split applicable';

      default:
        return '';
    }
  }
}
