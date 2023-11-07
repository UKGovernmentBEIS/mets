import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Subject } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER, TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { AviationAerSafPurchase } from 'pmrv-api';

import {
  aerEmissionsReductionClaimFormProvider,
  AviationAerSafPurchaseFormModel,
  SustainabilityCriteriaEvidenceType,
} from '../emissions-reduction-claim-form.provider';

@Injectable()
export class aerEmissionsReductionClaimBatchItemFormProvider
  implements TaskFormProvider<AviationAerSafPurchase, AviationAerSafPurchaseFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private requestTaskFileService = inject(RequestTaskFileService);
  private store = inject(RequestTaskStore);
  private route = inject(ActivatedRoute);
  private parentFormProvider = inject<aerEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);

  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(aerSafPurchase: AviationAerSafPurchase | undefined): void {
    if (aerSafPurchase) {
      const value = {
        ...aerSafPurchase,
        evidenceFiles:
          aerSafPurchase.evidenceFiles?.map((uuid) => ({
            file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
            uuid,
          })) ?? [],
      };

      this.form.patchValue(value);
    }
  }

  getFormValue(): AviationAerSafPurchase {
    const value = { ...this.form.value };
    if (value.evidenceFiles) {
      value.evidenceFiles = value.evidenceFiles.map((fu) => fu.uuid);
    }
    return value as AviationAerSafPurchase;
  }

  private buildForm() {
    const batchIndex = this.route.snapshot.queryParamMap.get('batchIndex');

    const formGroup = this.fb.group(
      {
        fuelName: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter a fuel name'),
            GovukValidators.maxLength(500, 'Enter up to 500 characters'),
          ],
        }),
        batchNumber: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter a batch number'),
            GovukValidators.maxLength(500, 'Enter up to 500 characters'),
          ],
        }),
        safMass: new FormControl<number>(null, {
          validators: [
            GovukValidators.required('Enter the total mass of sustainable aviation fuel claimed from the batch'),
            GovukValidators.positiveNumber('Enter a number greater than 0, correct to 3 decimal places'),
            GovukValidators.pattern('-?[0-9]*\\.?[0-9]{0,3}', 'Estimated tonnes must be to 3 decimal places'),
          ],
        }),
        sustainabilityCriteriaEvidenceType: new FormControl<SustainabilityCriteriaEvidenceType>(null, {
          validators: GovukValidators.required(
            'Select which evidence type shows that this. batch meets the sustainability criteria',
          ),
        }),
        otherSustainabilityCriteriaEvidenceDescription: new FormControl<string | null>(null, {
          validators: [
            GovukValidators.required(
              'Describe the other evidence you have to prove the batch meets the sustainability criteria',
            ),
            GovukValidators.maxLength(100, 'Enter up to 100 characters'),
          ],
        }),
        evidenceFiles: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.aerDelegate.payload.aerAttachments,
          'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
          true,
          !this.store.getState().isEditable,
        ) as FormControl<FileUpload[]>,
      },
      { updateOn: 'change' },
    );

    if (batchIndex) {
      const batchItemFormDetails = this.parentFormProvider.form.value.safDetails?.purchases[+batchIndex];
      formGroup.patchValue({ ...batchItemFormDetails });
    }

    return (this._form = formGroup);
  }
}
