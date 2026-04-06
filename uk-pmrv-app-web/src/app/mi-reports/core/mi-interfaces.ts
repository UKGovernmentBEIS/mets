import { MiReportSystemResult } from 'pmrv-api';

export interface ExtendedMiReportResult extends MiReportSystemResult {
  results: Array<any>;
}
