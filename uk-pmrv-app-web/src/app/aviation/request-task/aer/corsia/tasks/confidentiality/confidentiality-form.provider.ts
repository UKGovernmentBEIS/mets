import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { TaskSection } from '@shared/task-list/task-list.interface';
import { cloneDeep } from 'lodash-es';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaConfidentiality, RequestTaskDTO } from 'pmrv-api';

export interface ConfidentialityFormModel {
  totalEmissionsPublished: FormControl<boolean | null>;
  totalEmissionsExplanation?: FormControl<string | null>;
  totalEmissionsDocuments?: FormControl<FileUpload[]>;
  aggregatedStatePairDataPublished: FormControl<boolean | null>;
  aggregatedStatePairDataExplanation?: FormControl<string | null>;
  aggregatedStatePairDataDocuments?: FormControl<FileUpload[]>;
}

export interface ConfidentialityViewModel {
  heading: string;
  requestTask: RequestTaskDTO;
  sections: TaskSection<any>[];
  downloadUrl: string;
  submitHidden: boolean;
}

@Injectable()
export class ConfidentialityFormProvider
  implements TaskFormProvider<AviationAerCorsiaConfidentiality, ConfidentialityFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private store = inject(RequestTaskStore);
  private requestTaskFileService = inject(RequestTaskFileService);
  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this._buildForm();
    }
    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(confidentiality: AviationAerCorsiaConfidentiality | undefined): void {
    const value: any = cloneDeep(confidentiality);

    if (value) {
      this.form.get('totalEmissionsPublished').patchValue(value.totalEmissionsPublished);
      this.form.get('aggregatedStatePairDataPublished').patchValue(value.aggregatedStatePairDataPublished);

      if (value?.totalEmissionsExplanation) {
        this.form.get('totalEmissionsExplanation').patchValue(value.totalEmissionsExplanation);
      }

      if (value?.totalEmissionsDocuments) {
        this.form.get('totalEmissionsDocuments').patchValue(
          value.totalEmissionsDocuments?.map((uuid) => ({
            file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
            uuid,
          })) ?? [],
        );
      }

      if (value?.aggregatedStatePairDataExplanation) {
        this.form.get('aggregatedStatePairDataExplanation').patchValue(value.aggregatedStatePairDataExplanation);
      }

      if (value?.aggregatedStatePairDataDocuments) {
        this.form.get('aggregatedStatePairDataDocuments').patchValue(
          value.aggregatedStatePairDataDocuments?.map((uuid) => ({
            file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
            uuid,
          })) ?? [],
        );
      }
    }
  }

  getFormValue(): AviationAerCorsiaConfidentiality {
    const result = cloneDeep(this.form.value);

    let confidentiality: any = {
      ...result,
    };

    if (this.form.value.totalEmissionsPublished === true) {
      confidentiality = {
        ...confidentiality,
        totalEmissionsExplanation: result?.totalEmissionsExplanation,
        totalEmissionsDocuments: result?.totalEmissionsDocuments?.map((fu) => fu.uuid),
      };
    }

    if (this.form.value.aggregatedStatePairDataPublished === true) {
      confidentiality = {
        ...confidentiality,
        aggregatedStatePairDataExplanation: result?.aggregatedStatePairDataExplanation,
        aggregatedStatePairDataDocuments: result?.aggregatedStatePairDataDocuments?.map((fu) => fu.uuid),
      };
    }

    return confidentiality as AviationAerCorsiaConfidentiality;
  }

  get totalEmissionsPublishedCtrl(): FormControl {
    return this.form.get('totalEmissionsPublished') as FormControl;
  }

  get totalEmissionsExplanationCtrl(): FormControl {
    return this.form.get('totalEmissionsExplanation') as FormControl;
  }

  get totalEmissionsDocumentsCtrl(): FormControl {
    return this.form.get('totalEmissionsDocuments') as FormControl<FileUpload[]>;
  }

  get aggregatedStatePairDataPublishedCtrl(): FormControl {
    return this.form.get('aggregatedStatePairDataPublished') as FormControl;
  }

  get aggregatedStatePairDataExplanationCtrl(): FormControl {
    return this.form.get('aggregatedStatePairDataExplanation') as FormControl;
  }

  get aggregatedStatePairDataDocumentsCtrl(): FormControl {
    return this.form.get('aggregatedStatePairDataDocuments') as FormControl<FileUpload[]>;
  }

  _buildForm() {
    this._form = this.fb.group(
      {
        totalEmissionsPublished: new FormControl<boolean | null>(null, {
          validators: [
            GovukValidators.required(
              'Select if you want to request ICAO not publish your total annual emissions data at the operator level',
            ),
          ],
          updateOn: 'change',
        }),
        aggregatedStatePairDataPublished: new FormControl<boolean | null>(null, {
          validators: [GovukValidators.required('Select yes or no')],
          updateOn: 'change',
        }),
      },
      { updateOn: 'change' },
    );

    this.form
      .get('totalEmissionsPublished')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((totalEmissionsPublished) => {
        if (totalEmissionsPublished === true) {
          this._addTotalEmissionsDetails();
        } else {
          this._removeTotalEmissionsDetails();
        }

        this.form.updateValueAndValidity();
      });

    this.form
      .get('aggregatedStatePairDataPublished')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((aggregatedStatePairDataPublished) => {
        if (aggregatedStatePairDataPublished === true) {
          this._addAggregatedStatePairDataDetails();
        } else {
          this._removeAggregatedStatePairDataDetails();
        }

        this.form.updateValueAndValidity();
      });
  }

  private _addTotalEmissionsDetails() {
    if (!this.form.contains('totalEmissionsExplanation')) {
      this.form.addControl(
        'totalEmissionsExplanation',
        this.fb.control(null, [
          GovukValidators.required(
            'State the total emissions data you wish ICAO not to publish and say why disclosing this would harm your commercial interests',
          ),
        ]),
      );

      this.form.addControl(
        'totalEmissionsDocuments',
        this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.aerDelegate.payload.aerAttachments,
          'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
          false,
          !this.store.getState().isEditable,
        ) as FormControl<FileUpload[]>,
      );
    }
  }

  private _removeTotalEmissionsDetails() {
    if (this.form.contains('totalEmissionsExplanation')) {
      this.form.removeControl('totalEmissionsExplanation');
      this.form.removeControl('totalEmissionsDocuments');
    }
  }

  private _addAggregatedStatePairDataDetails() {
    if (!this.form.contains('aggregatedStatePairDataExplanation')) {
      this.form.addControl(
        'aggregatedStatePairDataExplanation',
        this.fb.control(null, [
          GovukValidators.required(
            'Give the state pair data you are requesting ICAO not to publish, and say why disclosing it would harm your commercial interests',
          ),
        ]),
      );

      this.form.addControl(
        'aggregatedStatePairDataDocuments',
        this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.aerDelegate.payload.aerAttachments,
          'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
          false,
          !this.store.getState().isEditable,
        ) as FormControl<FileUpload[]>,
      );
    }
  }

  private _removeAggregatedStatePairDataDetails() {
    if (this.form.contains('aggregatedStatePairDataExplanation')) {
      this.form.removeControl('aggregatedStatePairDataExplanation');
      this.form.removeControl('aggregatedStatePairDataDocuments');
    }
  }
}
