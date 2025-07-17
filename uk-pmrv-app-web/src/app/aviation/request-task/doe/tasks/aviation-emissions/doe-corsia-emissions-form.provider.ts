import { inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { dueDateMinValidatorToday } from '@tasks/permit-notification/follow-up/factory/form-provider';

import { GovukValidators } from 'govuk-components';

import { AviationDoECorsia, AviationDoECorsiaDeterminationReason } from 'pmrv-api';

export interface AviationDoeFormModel {
  determinationReason: FormGroup<DeterminationReasonFormModel | null>;
  emissions?: FormGroup<EmissionsFormModel>;
  chargeOperator?: FormGroup<{ chargeOperator: FormControl<boolean | null> }>;
  fee?: FormGroup<AviationDoeFeeDetailsFormModel>;
}

export interface AviationDoeFeeDetailsFormModel {
  totalBillableHours: FormControl<string | null>;
  hourlyRate: FormControl<string | null>;
  dueDate: FormControl<string | null>;
  comments?: FormControl<string | null>;
}

export interface DeterminationReasonFormModel {
  type: FormControl<AviationDoECorsiaDeterminationReason['type'] | null>;
  subtypes: FormControl<AviationDoECorsiaDeterminationReason['subtypes'] | null>;
  furtherDetails?: FormControl<string | null>;
}

export interface EmissionsFormModel {
  emissionsAllInternationalFlights?: FormControl<string | null>;
  emissionsFlightsWithOffsettingRequirements?: FormControl<string | null>;
  emissionsClaimFromCorsiaEligibleFuels?: FormControl<string | null>;
  calculationApproach?: FormControl<string | null>;
  supportingDocuments?: FormControl<FileUpload[]>;
}

export class DoeCorsiaEmissionsFormProvider implements TaskFormProvider<AviationDoECorsia, AviationDoeFormModel> {
  private fb = inject(FormBuilder);
  private _form: FormGroup<AviationDoeFormModel>;
  private requestTaskFileService = inject(RequestTaskFileService);
  private store = inject(RequestTaskStore);
  private destroy$ = new Subject<void>();

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get form(): FormGroup<AviationDoeFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  addSupportingDocuments() {
    const documents = this.form.getRawValue()?.emissions?.supportingDocuments;
    documents?.forEach((doc) => {
      this.store.doeDelegate.addDoeAttachment({ [doc.uuid]: doc.file.name });
    });
  }

  get determinationReasonCtrl(): FormGroup {
    return this.form.get('determinationReason') as FormGroup;
  }

  get emissionsCtrl(): FormGroup {
    return this.form.get('emissions') as FormGroup;
  }

  get chargeOperatorCtrl(): FormGroup {
    return this.form.get('chargeOperator') as FormGroup;
  }

  get feeCtrl(): FormGroup {
    return this.form.get('fee') as FormGroup;
  }

  emissionsAllInternationalFlightsValidators = [
    GovukValidators.required('Enter the total emissions on all international flights'),
    GovukValidators.naturalNumber('Enter positive whole number'),
  ];

  emissionsFlightsWithOffsettingRequirementsValidators = [
    GovukValidators.required('Enter the emissions from flights with offsetting requirements'),
    GovukValidators.integerNumber('Enter positive whole number (you can use zero)'),
  ];

  emissionsClaimFromCorsiaEligibleFuelsValidators = [
    GovukValidators.required('Enter the emissions related to a claim from CORSIA eligible fuels'),
    GovukValidators.integerNumber('Enter positive whole number (you can use zero)'),
  ];

  setFormValue(aviationEmissions: AviationDoECorsia | undefined): void {
    if (aviationEmissions?.fee?.chargeOperator) {
      this.addFee();
    } else {
      this.removeFee();
    }

    this.form.patchValue({
      determinationReason: aviationEmissions?.determinationReason ?? {},

      emissions: {
        emissionsAllInternationalFlights: aviationEmissions?.emissions?.emissionsAllInternationalFlights ?? null,
        emissionsFlightsWithOffsettingRequirements:
          aviationEmissions?.emissions?.emissionsFlightsWithOffsettingRequirements ?? null,
        emissionsClaimFromCorsiaEligibleFuels:
          aviationEmissions?.emissions?.emissionsClaimFromCorsiaEligibleFuels ?? null,
        calculationApproach: aviationEmissions?.emissions?.calculationApproach ?? null,
        supportingDocuments:
          aviationEmissions?.supportingDocuments?.map((uuid) => ({
            file: { name: this.store.doeDelegate.payload?.doeAttachments?.[uuid] } as File,
            uuid,
          })) ?? [],
      },

      chargeOperator: {
        chargeOperator: aviationEmissions?.fee?.chargeOperator ?? null,
      },
      fee: {
        totalBillableHours: aviationEmissions?.fee?.feeDetails?.totalBillableHours ?? null,
        hourlyRate: aviationEmissions?.fee?.feeDetails?.hourlyRate ?? null,
        dueDate: aviationEmissions?.fee?.feeDetails?.dueDate
          ? (new Date(aviationEmissions.fee.feeDetails.dueDate) as any)
          : null,
        comments: aviationEmissions?.fee?.feeDetails?.comments ?? null,
      },
    });

    this.determinationReasonCtrl
      .get('type')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value === 'CORRECTIONS_TO_A_VERIFIED_REPORT') {
          this.determinationReasonCtrl
            .get('subtypes')
            .setValidators([GovukValidators.required('Select the emissions you are correcting')]);
        } else {
          this.determinationReasonCtrl.get('subtypes').clearValidators();
        }
        this.determinationReasonCtrl.get('subtypes').updateValueAndValidity();
      });

    this.determinationReasonCtrl?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value?.type === 'VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED') {
        if (this.emissionsCtrl && !this.emissionsCtrl.contains('emissionsAllInternationalFlights')) {
          this.emissionsCtrl.addControl(
            'emissionsAllInternationalFlights',
            this.fb.control(
              this.form.value.emissions?.emissionsAllInternationalFlights ?? null,
              this.emissionsAllInternationalFlightsValidators,
            ),
          );
        }

        if (this.emissionsCtrl && !this.emissionsCtrl.contains('emissionsFlightsWithOffsettingRequirements')) {
          this.emissionsCtrl.addControl(
            'emissionsFlightsWithOffsettingRequirements',
            this.fb.control(
              this.form.value.emissions?.emissionsFlightsWithOffsettingRequirements ?? null,
              this.emissionsFlightsWithOffsettingRequirementsValidators,
            ),
          );
        }

        if (this.emissionsCtrl && !this.emissionsCtrl.contains('emissionsClaimFromCorsiaEligibleFuels')) {
          this.emissionsCtrl.addControl(
            'emissionsClaimFromCorsiaEligibleFuels',
            this.fb.control(
              this.form.value.emissions?.emissionsClaimFromCorsiaEligibleFuels ?? null,
              this.emissionsClaimFromCorsiaEligibleFuelsValidators,
            ),
          );
        }
      } else if (value.type === 'CORRECTIONS_TO_A_VERIFIED_REPORT') {
        if (value?.subtypes?.includes('CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS')) {
          if (this.emissionsCtrl && !this.emissionsCtrl.contains('emissionsAllInternationalFlights')) {
            this.emissionsCtrl.addControl(
              'emissionsAllInternationalFlights',
              this.fb.control(
                this.form.value.emissions?.emissionsAllInternationalFlights ?? null,
                this.emissionsAllInternationalFlightsValidators,
              ),
            );
          }
        } else if (value?.subtypes && this.emissionsCtrl.contains('emissionsAllInternationalFlights')) {
          this.emissionsCtrl.removeControl('emissionsAllInternationalFlights');
        }

        if (value?.subtypes?.includes('CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS')) {
          if (this.emissionsCtrl && !this.emissionsCtrl.contains('emissionsFlightsWithOffsettingRequirements')) {
            this.emissionsCtrl.addControl(
              'emissionsFlightsWithOffsettingRequirements',
              this.fb.control(
                this.form.value.emissions?.emissionsFlightsWithOffsettingRequirements ?? null,
                this.emissionsFlightsWithOffsettingRequirementsValidators,
              ),
            );
          }
        } else if (value?.subtypes && this.emissionsCtrl.contains('emissionsFlightsWithOffsettingRequirements')) {
          this.emissionsCtrl.removeControl('emissionsFlightsWithOffsettingRequirements');
        }

        if (value?.subtypes?.includes('CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS')) {
          if (this.emissionsCtrl && !this.emissionsCtrl.contains('emissionsClaimFromCorsiaEligibleFuels')) {
            this.emissionsCtrl.addControl(
              'emissionsClaimFromCorsiaEligibleFuels',
              this.fb.control(
                this.form.value.emissions?.emissionsClaimFromCorsiaEligibleFuels ?? null,
                this.emissionsClaimFromCorsiaEligibleFuelsValidators,
              ),
            );
          }
        } else if (value?.subtypes && this.emissionsCtrl.contains('emissionsClaimFromCorsiaEligibleFuels')) {
          this.emissionsCtrl.removeControl('emissionsClaimFromCorsiaEligibleFuels');
        }
      } else {
        this.determinationReasonCtrl.get('subtypes').clearValidators();
      }
      this.emissionsCtrl.updateValueAndValidity();
    });

    this.determinationReasonCtrl.get('type').updateValueAndValidity({ emitEvent: true });
    this.determinationReasonCtrl.updateValueAndValidity({ emitEvent: true });
  }

  getFormValue(): AviationDoECorsia {
    const value = this.form.value;
    let ret: any = {
      determinationReason: value.determinationReason,
    };

    if (value?.emissions) {
      ret = {
        ...ret,
        emissions: {
          emissionsAllInternationalFlights: value.emissions?.emissionsAllInternationalFlights,
          emissionsFlightsWithOffsettingRequirements: value.emissions?.emissionsFlightsWithOffsettingRequirements,
          emissionsClaimFromCorsiaEligibleFuels: value.emissions?.emissionsClaimFromCorsiaEligibleFuels,
          calculationApproach: value.emissions?.calculationApproach,
        },
      };

      ret.supportingDocuments = (value.emissions?.supportingDocuments || [])
        ?.map((fu) => fu.uuid)
        .filter((uuid) => uuid);
    }

    if (typeof value.chargeOperator.chargeOperator === 'boolean') {
      ret.fee = {
        chargeOperator: value.chargeOperator.chargeOperator,
      };
    }

    value?.chargeOperator?.chargeOperator ? this.addFee() : this.removeFee();

    if (value?.fee?.totalBillableHours && value?.chargeOperator?.chargeOperator) {
      ret.fee.feeDetails = {
        totalBillableHours: value.fee.totalBillableHours,
        hourlyRate: value.fee.hourlyRate,
        dueDate: value.fee.dueDate,
        comments: value.fee.comments,
      };
    }

    return ret;
  }

  private addFee() {
    if (!this.form.contains('fee')) {
      const commonMessage = 'Enter a whole number with up to 2 decimal places';
      this.form.addControl(
        'fee',
        this.fb.group<AviationDoeFeeDetailsFormModel>({
          totalBillableHours: new FormControl<string | null>(null, {
            updateOn: 'change',
            validators: [
              GovukValidators.required('Enter the total billable hours'),
              GovukValidators.pattern('^(?!0\\.00)([1-9][0-9]*(\\.[0-9]{1,2})?|0\\.[0-9]{1,2})$', commonMessage),
              GovukValidators.notNaN(commonMessage),
            ],
          }),
          hourlyRate: new FormControl<string | null>(null, {
            updateOn: 'change',
            validators: [
              GovukValidators.required('Enter the hourly rate'),
              GovukValidators.min(0, commonMessage),
              GovukValidators.pattern('^(?!0\\.00)([1-9][0-9]*(\\.[0-9]{1,2})?|0\\.[0-9]{1,2})$', commonMessage),
              GovukValidators.notNaN(commonMessage),
            ],
          }),
          dueDate: new FormControl<string | null>(null, {
            updateOn: 'change',
            validators: [GovukValidators.required('Enter a full date'), dueDateMinValidatorToday()],
          }),
          comments: new FormControl<string | null>(null, {
            updateOn: 'change',
            validators: [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
          }),
        }),
      );
    }
  }

  private removeFee() {
    if (this.form.contains('fee')) {
      this.form.removeControl('fee');
    }
  }

  private buildForm() {
    const formGroup = this.fb.group<AviationDoeFormModel>(
      {
        determinationReason: new FormGroup<DeterminationReasonFormModel | null>({
          type: new FormControl<AviationDoECorsiaDeterminationReason['type'] | null>(null, {
            updateOn: 'change',
            validators: [
              GovukValidators.required(
                'Select if a verified emissions report has not be submitted, or you are correcting a verified report',
              ),
            ],
          }),
          subtypes: new FormControl<AviationDoECorsiaDeterminationReason['subtypes'] | null>(null, {
            updateOn: 'change',
          }),
          furtherDetails: new FormControl<string | null>(null, [
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ]),
        }),
        emissions: new FormGroup<EmissionsFormModel | null>(
          {
            emissionsAllInternationalFlights: new FormControl<string | null>(null, {
              validators: this.emissionsAllInternationalFlightsValidators,
            }),
            emissionsFlightsWithOffsettingRequirements: new FormControl<string | null>(null, {
              validators: this.emissionsFlightsWithOffsettingRequirementsValidators,
            }),
            emissionsClaimFromCorsiaEligibleFuels: new FormControl<string | null>(null, {
              validators: this.emissionsClaimFromCorsiaEligibleFuelsValidators,
            }),
            calculationApproach: new FormControl<string | null>(null, [
              GovukValidators.required('Explain how you have estimated/corrected the emissions'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
            supportingDocuments: this.store.doeDelegate?.payload?.doeAttachments
              ? (this.requestTaskFileService.buildFormControl(
                  this.store.requestTaskId,
                  [],
                  this.store.doeDelegate.payload.doeAttachments,
                  'AVIATION_DOE_CORSIA_UPLOAD_ATTACHMENT',
                  false,
                  !this.store.getState().isEditable,
                ) as FormControl<FileUpload[]>)
              : new FormControl<FileUpload[]>([]),
          },
          {
            validators: [offsettingLowerThanInternationalValidator()],
            updateOn: 'change',
          },
        ),
        chargeOperator: new FormGroup<{ chargeOperator: FormControl<boolean | null> }>({
          chargeOperator: new FormControl<boolean | null>(null, {
            validators: [GovukValidators.required('Select yes if you need to charge the operator a fee')],
          }),
        }),
      },
      { updateOn: 'change' },
    );

    return (this._form = formGroup);
  }
}

export const offsettingLowerThanInternationalValidator = (): ValidatorFn => {
  return GovukValidators.builder(
    'Emissions for international flights must be equal to or greater than emissions from flights with offsetting requirements',
    (group: UntypedFormGroup) => {
      const emissionsAllInternationalFlights = +group?.controls?.emissionsAllInternationalFlights?.value;
      const emissionsFlightsWithOffsettingRequirements =
        +group?.controls?.emissionsFlightsWithOffsettingRequirements?.value;

      const bothControlsExist =
        !!group?.controls?.emissionsFlightsWithOffsettingRequirements &&
        !!group?.controls?.emissionsAllInternationalFlights &&
        emissionsFlightsWithOffsettingRequirements !== null &&
        emissionsFlightsWithOffsettingRequirements !== undefined &&
        emissionsAllInternationalFlights;

      const controlMissing =
        !group?.controls?.emissionsFlightsWithOffsettingRequirements ||
        !group?.controls?.emissionsAllInternationalFlights;

      const internationalGreater = emissionsAllInternationalFlights >= emissionsFlightsWithOffsettingRequirements;
      const valid = (bothControlsExist && internationalGreater) || controlMissing;

      return valid ? null : { lowerThanInternational: true };
    },
  );
};
