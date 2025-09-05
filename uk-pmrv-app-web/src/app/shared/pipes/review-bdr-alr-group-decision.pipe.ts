import { Pipe, PipeTransform } from '@angular/core';

import { ALRAlrDataRegulatorReviewDecision, BDRBdrDataRegulatorReviewDecision } from 'pmrv-api';

@Pipe({ name: 'reviewBdrAlrGroupDecision', standalone: true })
export class ReviewBdrAlrGroupDecisionPipe implements PipeTransform {
  transform(value: BDRBdrDataRegulatorReviewDecision['type'] | ALRAlrDataRegulatorReviewDecision['type']): string {
    switch (value) {
      case 'ACCEPTED':
        return 'Accepted';
      case 'OPERATOR_AMENDS_NEEDED':
        return 'Operator amendments needed';
    }
  }
}
