import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { aerQuery } from '../../aer.selectors';
import { ReportingObligation } from '../reporting-obligation.interface';

const selectReportingObligation: OperatorFunction<RequestTaskState, ReportingObligation> = pipe(
  aerQuery.selectReportingObligation,
  map((aer) => {
    return {
      reportingRequired: aer.reportingRequired,
      reportingObligationDetails: aer?.reportingObligationDetails,
    } as ReportingObligation;
  }),
);

export const reportingObligationQuery = {
  selectReportingObligation,
};
