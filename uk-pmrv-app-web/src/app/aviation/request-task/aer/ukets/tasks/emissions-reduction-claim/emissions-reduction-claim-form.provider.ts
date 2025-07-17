import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ValidatorFn } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { format } from '@shared/utils/bignumber.utils';
import BigNumber from 'bignumber.js';
import { cloneDeep } from 'lodash-es';

import { GovukValidators } from 'govuk-components';

import { AviationAerSaf, AviationAerSafDetails, AviationAerSafPurchase } from 'pmrv-api';

import { TaskFormProvider } from '../../../../task-form.provider';

export interface AviationAerSafPurchaseFormModel {
  fuelName: FormControl<string | null>;
  batchNumber: FormControl<string | null>;
  safMass: FormControl<number | null>;
  sustainabilityCriteriaEvidenceType: FormControl<SustainabilityCriteriaEvidenceType>;
  otherSustainabilityCriteriaEvidenceDescription: FormControl<string | null>;
  evidenceFiles: FormControl<FileUpload[]>;
}
export type AviationAerSafPurchaseType = FormGroup<AviationAerSafPurchaseFormModel>;
export type SustainabilityCriteriaEvidenceType =
  | 'SCREENSHOT_FROM_RTFO_REGISTRY'
  | 'VOLUNTARY_CERTIFICATION_SCHEME_MARKING'
  | 'FUEL_SUPPLIER_REPORT'
  | 'OTHER'
  | null;

export interface AviationAerSafDetailsFormModel {
  purchases: FormArray<FormGroup<AviationAerSafPurchaseFormModel>>;
  noDoubleCountingDeclarationFile: FormControl<FileUpload>;
  totalSafMass: FormControl<string>;
  emissionsFactor: FormControl<string>;
  totalEmissionsReductionClaim: FormControl<string>;
}

export interface EmissionsReductionClaimFormModel {
  exist: FormControl<boolean | null>;
  safDetails?: FormGroup<AviationAerSafDetailsFormModel>;
}

@Injectable()
export class AerEmissionsReductionClaimFormProvider
  implements TaskFormProvider<AviationAerSaf, EmissionsReductionClaimFormModel>
{
  private readonly fb = inject(FormBuilder);
  private _form: FormGroup;
  private readonly store = inject(RequestTaskStore);
  private readonly requestTaskFileService = inject(RequestTaskFileService);

  private readonly destroy$ = new Subject<void>();

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

  setFormValue(aerSaf: AviationAerSaf | undefined): void {
    const value: any = cloneDeep(aerSaf);
    if (value) {
      this.form.get('exist').patchValue(value.exist);
      const arrayValue = value.safDetails?.purchases?.map((val) => this.createSafPurchase(val));
      if (value.safDetails?.purchases?.length) {
        (this.form.get('safDetails') as FormGroup).setControl('purchases', this.fb.array(arrayValue ?? []));
      }

      if (value.safDetails?.noDoubleCountingDeclarationFile) {
        const declarationFile = value.safDetails?.noDoubleCountingDeclarationFile;
        (this.form.get('safDetails') as FormGroup).get('noDoubleCountingDeclarationFile').patchValue({
          file: { name: this.store.aerDelegate.payload.aerAttachments[declarationFile] } as File,
          uuid: declarationFile,
        });
      }
      this.setTotalEmissions();
    }
  }

  getFormValue(): AviationAerSaf {
    this.setTotalEmissions();
    const result = cloneDeep(this.form.value);

    if (result.safDetails) {
      result.safDetails?.purchases.forEach((purchase, index) => {
        if (purchase.evidenceFiles) {
          result.safDetails.purchases[index].evidenceFiles = purchase.evidenceFiles.map((fu) => fu.uuid);
        }
        result.safDetails.purchases[index].safMass = '' + result.safDetails.purchases[index].safMass;
      });
      result.safDetails.noDoubleCountingDeclarationFile = result?.safDetails?.noDoubleCountingDeclarationFile?.uuid;
    }
    return result as AviationAerSaf;
  }

  setTotalEmissions() {
    const totalMass = new BigNumber(this.calculateTotalEmissions());
    const totalEmissions = totalMass.multipliedBy(3.15);
    this.safDetails?.patchValue({
      emissionsFactor: '3.15',
      totalSafMass: format(totalMass, 3),
      totalEmissionsReductionClaim: format(totalEmissions, 3),
    });
  }

  calculateTotalEmissions(): BigNumber {
    let total = new BigNumber(0);
    this._form.value.safDetails?.purchases?.forEach(
      (purchase) => (total = new BigNumber(purchase.safMass).plus(total)),
    );
    return total;
  }

  get existCtrl(): FormControl {
    return this.form.get('exist') as FormControl;
  }

  get declarationFileCtlr(): FormGroup {
    return this.safDetails.get('noDoubleCountingDeclarationFile') as FormGroup;
  }

  get safDetails(): FormGroup {
    return this.form.get('safDetails') as FormGroup;
  }

  get safPurchases(): FormArray {
    return this.safDetails?.controls?.purchases as FormArray;
  }

  private createSafPurchase(purchase?: AviationAerSafPurchase, validators: ValidatorFn[] = []): FormGroup {
    return this.fb.group(
      {
        fuelName: [purchase?.fuelName ?? null],
        batchNumber: [purchase?.batchNumber ?? null],
        safMass: [Number(purchase?.safMass) || null],
        sustainabilityCriteriaEvidenceType: [purchase?.sustainabilityCriteriaEvidenceType ?? null],
        otherSustainabilityCriteriaEvidenceDescription: [
          purchase?.otherSustainabilityCriteriaEvidenceDescription ?? null,
        ],
        evidenceFiles: this.fb.array(
          purchase?.evidenceFiles?.map((uuid) => ({
            file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
            uuid,
          })) ?? [],
        ),
      },
      { updateOn: 'change', validators },
    );
  }

  private safDetailsCreateInitialForm() {
    return this.fb.group(
      {
        purchases: this.fb.array([]),
        noDoubleCountingDeclarationFile: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          null,
          this.store.aerDelegate.payload.aerAttachments,
          'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
          true,
          false,
        ) as FormControl<FileUpload>,
        totalSafMass: new FormControl(null),
        emissionsFactor: new FormControl(null),
        totalEmissionsReductionClaim: new FormControl(null),
        // purchases: this.fb.array([this.createSafPurchase(null)]),
      },
      { updateOn: 'change' },
    ) as unknown as FormGroup<Record<keyof AviationAerSafDetails, FormControl>>;
  }

  addSafDetailsCtrl() {
    this.form.addControl('safDetails', this.safDetailsCreateInitialForm());
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        exist: new FormControl<boolean | null>(null, [
          GovukValidators.required('Select yes if you will be making an emissions reduction claim'),
        ]),
      },
      { updateOn: 'change' },
    );

    this.existCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exists) => {
      if (exists) {
        this.addSafDetailsCtrl();
      } else {
        this.form.removeControl('safDetails');
      }
    });
  }
}
