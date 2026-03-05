import { inject, Injectable } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { RequestTaskStore } from '../../../store';
import { TaskFormProvider } from '../../../task-form.provider';
import {
  ReportingObligation,
  ReportingObligationDetailsFormModel,
  ReportingObligationFormModel,
} from './reporting-obligation.interface';

@Injectable()
export class ReportingObligationFormProvider
  implements TaskFormProvider<ReportingObligation, ReportingObligationFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<ReportingObligationFormModel>;
  private store = inject(RequestTaskStore);
  private requestTaskFileService = inject(RequestTaskFileService);
  private destroy$ = new Subject<void>();

  get form(): FormGroup<ReportingObligationFormModel> {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get reportingRequiredCtrl(): AbstractControl {
    return this.form.get('reportingRequired');
  }

  get reportingObligationDetailsForm(): FormGroup<ReportingObligationDetailsFormModel> {
    return this.form.get('reportingObligationDetails') as FormGroup;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(reporting: ReportingObligation | undefined) {
    if (reporting) {
      this.reportingRequiredCtrl.setValue(reporting.reportingRequired);

      if (reporting.reportingRequired === false) {
        this.reportingObligationDetailsForm.setValue({
          noReportingReason: reporting.reportingObligationDetails?.noReportingReason,
          supportingDocuments:
            reporting.reportingObligationDetails?.supportingDocuments?.map((uuid) => ({
              file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
              uuid,
            })) ?? [],
        });
      }
    }
  }

  getFormValue(): ReportingObligation {
    const value = this._form.value;

    let ret: any = {
      ...value,
    };

    if (value.reportingObligationDetails?.supportingDocuments) {
      ret = {
        ...value,
        reportingObligationDetails: {
          noReportingReason: value.reportingObligationDetails?.noReportingReason,
          supportingDocuments: value.reportingObligationDetails?.supportingDocuments?.map((fu) => fu.uuid),
        },
      };
    }

    return ret as ReportingObligation;
  }

  private _buildForm() {
    this._form = this.fb.group<ReportingObligationFormModel>({
      reportingRequired: new FormControl<ReportingObligation['reportingRequired'] | null>(null, {
        updateOn: 'change',
        validators: GovukValidators.required('Select yes or no'),
      }),
    });

    this.reportingRequiredCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((reportingRequired) => {
      if (reportingRequired === false) {
        this._addDetails();
      } else {
        this._removeDetails();
      }
    });
  }

  private _addDetails() {
    if (!this._form.contains('reportingObligationDetails')) {
      this._form.addControl(
        'reportingObligationDetails',
        this.fb.group<ReportingObligationDetailsFormModel>(
          {
            noReportingReason: new FormControl<string | null>(null, {
              validators: GovukValidators.required(
                'Please provide information to support your reason for not reporting.',
              ),
            }),
            supportingDocuments: this.requestTaskFileService.buildFormControl(
              this.store.requestTaskId,
              [],
              this.store.aerDelegate.payload.aerAttachments,
              'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
              false,
              !this.store.getState().isEditable,
            ),
          },
          { validators: Validators.required },
        ),
      );
    }
  }

  private _removeDetails() {
    if (this._form.contains('reportingObligationDetails')) {
      this._form.removeControl('reportingObligationDetails');
    }
  }
}
