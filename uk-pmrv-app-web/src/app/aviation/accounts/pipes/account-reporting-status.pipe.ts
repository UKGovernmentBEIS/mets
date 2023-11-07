import { Pipe, PipeTransform } from '@angular/core';

import { AviationAccountDetails } from '../store/aviation-accounts.state';

const REPORTING_STATUS_MAP = {
  EXEMPT_COMMERCIAL: 'Exempt (commercial)',
  EXEMPT_NON_COMMERCIAL: 'Exempt (non commercial)',
  REQUIRED_TO_REPORT: 'Required to report',
};

@Pipe({
  name: 'accountReportingStatus',
  pure: true,
})
export class AccountReportingStatusPipe implements PipeTransform {
  transform(value: AviationAccountDetails['reportingStatus']): string | null {
    if (value == null) {
      return null;
    }

    return REPORTING_STATUS_MAP[value] ?? null;
  }
}
