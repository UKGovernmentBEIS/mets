import { Pipe, PipeTransform } from '@angular/core';

import { AccountDetailsHistoryDTO } from 'pmrv-api';

@Pipe({
  name: 'accountDetailsHistoryCategory',
})
export class AviationAccoundDetailsHistoryCategoryPipe implements PipeTransform {
  transform(value: AccountDetailsHistoryDTO['category']): string {
    switch (value) {
      case 'FIRST_YEAR_OF_REPORTING_OBLIGATION':
        return 'First year of reporting obligation';
      case 'FIRST_YEAR_WITHIN_SCOPE_OF_APPLICABILITY':
        return 'First year within the scope of applicability';
      default:
        return '';
    }
  }
}
