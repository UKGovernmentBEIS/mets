import { FormControl, FormGroup } from '@angular/forms';

import { FileUpload } from '@shared/file-input/file-upload-event';
import { TaskSection } from '@shared/task-list/task-list.interface';

import { AviationAerReportingObligationDetails, RequestTaskDTO } from 'pmrv-api';

export interface ReportingObligation {
  reportingRequired: boolean;
  reportingObligationDetails?: AviationAerReportingObligationDetails;
}

export interface ReportingObligationViewModel {
  heading: string;
  year: number;
  isCorsia: boolean;
  requestTask: RequestTaskDTO;
  sections: TaskSection<any>[];
  downloadUrl: string;
  submitHidden: boolean;
}

export interface ReportingObligationDetailsFormModel {
  noReportingReason: FormControl<string | null>;
  supportingDocuments?: FormControl<FileUpload[]>;
}

export interface ReportingObligationFormModel {
  reportingRequired: FormControl<boolean | null>;
  reportingObligationDetails?: FormGroup<ReportingObligationDetailsFormModel>;
}
