import { MiReportResult } from 'pmrv-api';

export interface ExtendedMiReportResult extends MiReportResult {
  results: Array<any>;
}
