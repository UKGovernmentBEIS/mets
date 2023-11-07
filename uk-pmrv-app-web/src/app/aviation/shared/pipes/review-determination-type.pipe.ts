import { Pipe, PipeTransform } from '@angular/core';

import { EmpIssuanceDetermination, EmpVariationDetermination } from 'pmrv-api';

@Pipe({
  name: 'empReviewDeterminationType',
  pure: true,
  standalone: true,
})
export class EmpReviewDeterminationTypePipe implements PipeTransform {
  transform(type: EmpIssuanceDetermination['type'] | EmpVariationDetermination['type']): string | null {
    if (type === 'APPROVED') {
      return 'Approve';
    } else if (type === 'REJECTED') {
      return 'Reject';
    } else {
      return 'Withdraw';
    }
  }
}
