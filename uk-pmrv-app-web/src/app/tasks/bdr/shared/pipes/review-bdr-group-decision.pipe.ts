import { Pipe, PipeTransform } from '@angular/core';

import { BDRBdrDataRegulatorReviewDecision } from 'pmrv-api';

@Pipe({ name: 'reviewBdrGroupDecision', standalone: true })
export class ReviewBdrGroupDecisionPipe implements PipeTransform {
  transform(value: BDRBdrDataRegulatorReviewDecision['type']): string {
    switch (value) {
      case 'ACCEPTED':
        return 'Accepted';
      case 'OPERATOR_AMENDS_NEEDED':
        return 'Operator amendments needed';
    }
  }
}
