import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { todayOrPastDateValidator } from '@tasks/aer/verification-submit/opinion-statement/validators/today-or-past-date.validator';

import { GovukValidators } from 'govuk-components';

import {
  AviationAerInPersonSiteVisitDates,
  AviationAerOpinionStatement,
  AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { AviationAerOpinionStatementFormModel } from './opinion-statement.interface';

export interface AviationAerInPersonSiteVisitFormModel {
  visitDates: FormControl<AviationAerInPersonSiteVisitDates[]>;
  teamMembers: FormControl<string | null>;
}
export type AviationAerInPersonSiteVisitType = FormGroup<AviationAerInPersonSiteVisitFormModel>;

const manuallyProvidedEmissionsValidators = [
  GovukValidators.required('Enter the total verified emissions for the scheme year'),
  GovukValidators.integerNumber('Enter a whole number without decimal places (you can use zero)'),
];

const additionalChangesNotCoveredDetailsValidators = [
  GovukValidators.required('Give details of the changes that were not covered in the operator’s emissions report'),
];

@Injectable()
export class OpinionStatementFormProvider
  implements TaskFormProvider<AviationAerOpinionStatement, AviationAerOpinionStatementFormModel>
{
  private store = inject(RequestTaskStore);
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get form() {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get emissionsGroup() {
    return this.form.get('emissionsGroup') as FormGroup;
  }

  get emissionsCorrectCtrl() {
    return this.emissionsGroup.get('emissionsCorrect');
  }

  get changesGroup() {
    return this.form.get('changesGroup') as FormGroup;
  }

  get additionalChangesNotCoveredCtrl() {
    return this.changesGroup.get('additionalChangesNotCovered');
  }

  get siteVisitGroup() {
    return this.form.get('siteVisit') as FormGroup;
  }

  get virtualSiteVisitGroup() {
    return this.form.get('virtualSiteVisitGroup') as FormGroup;
  }

  get inPersonSiteVisitGroup() {
    return this.form.get('inPersonSiteVisitGroup') as FormGroup;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(opinionStatement: AviationAerOpinionStatement | undefined) {
    if (opinionStatement) {
      // Set initial form structure
      let value: any = {
        emissionsGroup: {
          fuelTypes: opinionStatement.fuelTypes ?? null,
          monitoringApproachType: opinionStatement.monitoringApproachType ?? null,
          emissionsCorrect: opinionStatement.emissionsCorrect ?? null,
        },
        changesGroup: {
          additionalChangesNotCovered:
            opinionStatement.additionalChangesNotCovered != null ? opinionStatement.additionalChangesNotCovered : null,
        },
      };

      if (opinionStatement?.siteVisit) {
        value = {
          ...value,
          siteVisit: {
            type: opinionStatement.siteVisit?.type ?? null,
          },
        };
      }

      // Conditionally add or remove manuallyProvidedEmissions control
      if (opinionStatement.emissionsCorrect === false) {
        this._addManuallyProvidedEmissions();

        value = {
          ...value,
          emissionsGroup: {
            ...value.emissionsGroup,
            manuallyProvidedEmissions: opinionStatement.manuallyProvidedEmissions ?? null,
          },
        };
      } else {
        this._removeManuallyProvidedEmissions();
      }

      // Conditionally add or remove additionalChangesNotCovered control
      if (opinionStatement.additionalChangesNotCovered === true) {
        this._addAdditionalChangesNotCovered();

        value = {
          ...value,
          changesGroup: {
            ...value.changesGroup,
            additionalChangesNotCoveredDetails: opinionStatement.additionalChangesNotCoveredDetails ?? null,
          },
        };
      } else {
        this._removeAdditionalChangesNotCovered();
      }

      // Add virtual site according to siteVisit type
      if (value?.siteVisit?.type === 'VIRTUAL') {
        this.addVirtualSiteGroup();

        value = {
          ...value,
          siteVisit: {
            ...value.siteVisit,
          },
          virtualSiteVisitGroup: {
            reason: opinionStatement.siteVisit['reason'] ?? null,
          },
        };
      }

      // Add in person site according to siteVisit type
      if (value?.siteVisit?.type === 'IN_PERSON') {
        this.addInPersonSiteGroup();

        value = {
          ...value,
          siteVisit: {
            ...value.siteVisit,
          },
          inPersonSiteVisitGroup: {
            visitDates: [],
            teamMembers: opinionStatement.siteVisit['teamMembers'] ?? null,
          },
        };
      }

      this.form.patchValue(value);

      (this.inPersonSiteVisitGroup?.controls?.visitDates as FormArray)?.clear();
      opinionStatement.siteVisit?.['visitDates']?.forEach((element) => {
        (this.inPersonSiteVisitGroup?.controls?.visitDates as FormArray)?.push(this._createVisitDatesGroup(element));
      });
    }
  }

  getFormValue(): AviationAerOpinionStatement {
    // Set initial structure of return form value
    let value: any = {
      fuelTypes: this.emissionsGroup?.value.fuelTypes ?? null,
      monitoringApproachType: this.emissionsGroup?.value.monitoringApproachType ?? null,
      emissionsCorrect: this.emissionsGroup?.value.emissionsCorrect ?? null,
      additionalChangesNotCovered: this.changesGroup?.value.additionalChangesNotCovered ?? null,
    };

    if (this.siteVisitGroup.value?.type) {
      value = {
        ...value,
        siteVisit: {
          type: this.siteVisitGroup.value.type ?? null,
        },
      };
    }

    // Conditionally set manuallyProvidedEmissions value
    if (this.emissionsGroup?.value.emissionsCorrect === false) {
      value = {
        ...value,
        manuallyProvidedEmissions: this.emissionsGroup?.value.manuallyProvidedEmissions ?? null,
      };
    }

    // Conditionally set additionalChangesNotCoveredDetails value
    if (this.changesGroup?.value.additionalChangesNotCovered === true) {
      value = {
        ...value,
        additionalChangesNotCoveredDetails: this.changesGroup?.value.additionalChangesNotCoveredDetails ?? null,
      };
    }

    // Set reason value if siteVisit type is VIRTUAL
    if (this.siteVisitGroup?.value?.type === 'VIRTUAL') {
      value = {
        ...value,
        siteVisit: {
          ...value.siteVisit,
          reason: this.virtualSiteVisitGroup?.value.reason ?? null,
        },
      };
    }

    // Set visitDates and teamMembers values if siteVisit type is IN_PERSON
    if (this.siteVisitGroup?.value?.type === 'IN_PERSON') {
      value = {
        ...value,
        siteVisit: {
          ...value.siteVisit,
          visitDates: this.inPersonSiteVisitGroup?.value.visitDates ?? [],
          teamMembers: this.inPersonSiteVisitGroup?.value.teamMembers ?? null,
        },
      };
    }

    return value as AviationAerOpinionStatement;
  }

  addVirtualSiteGroup() {
    if (this.form.contains('inPersonSiteVisitGroup')) {
      this.form.removeControl('inPersonSiteVisitGroup');
    }

    if (!this.form.contains('virtualSiteVisitGroup')) {
      this.form.addControl(
        'virtualSiteVisitGroup',
        this.fb.group({
          reason: new FormControl<string | null>(null, [
            GovukValidators.required('Explain the reasons why a virtual site visit was made'),
          ]),
        }),
      );
    }
  }

  addInPersonSiteGroup() {
    if (this.form.contains('virtualSiteVisitGroup')) {
      this.form.removeControl('virtualSiteVisitGroup');
    }

    if (!this.form.contains('inPersonSiteVisitGroup')) {
      this.form.addControl(
        'inPersonSiteVisitGroup',
        this.fb.group({
          visitDates: this.fb.array([], {
            validators: GovukValidators.required(''),
          }),

          teamMembers: new FormControl<string | null>(null, [
            GovukValidators.required('State which team members made the site visit'),
          ]),
        }),
      );
    }
  }

  private _buildForm() {
    const state = this.store.getState();

    const fuelTypes = (
      state.requestTaskItem.requestTask.payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.opinionStatement?.fuelTypes;

    this._form = this.fb.group(
      {
        emissionsGroup: this.fb.group(
          {
            fuelTypes: new FormControl<AviationAerOpinionStatement['fuelTypes'] | null>(fuelTypes, [
              GovukValidators.required('Select at least one fuel type'),
            ]),

            monitoringApproachType: new FormControl<AviationAerOpinionStatement['monitoringApproachType'] | null>(
              null,
              [GovukValidators.required('Select one approach')],
            ),

            emissionsCorrect: new FormControl<AviationAerOpinionStatement['emissionsCorrect'] | null>(null, {
              validators: [GovukValidators.required('Select if the reported emissions are correct')],
              updateOn: 'change',
            }),
          },
          { updateOn: 'change' },
        ),

        changesGroup: this.fb.group({
          additionalChangesNotCovered: new FormControl<
            AviationAerOpinionStatement['additionalChangesNotCovered'] | null
          >(null, {
            validators: [
              GovukValidators.required('Select if there were any other changes not covered in the emissions report'),
            ],
            updateOn: 'change',
          }),
        }),

        siteVisit: this.fb.group({
          type: new FormControl<AviationAerOpinionStatement['siteVisit']['type'] | null>(null, [
            GovukValidators.required('Select if your team made any in-person or virtual site visits'),
          ]),
        }),
      },
      { updateOn: 'change' },
    );

    this.emissionsCorrectCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value) {
        this._removeManuallyProvidedEmissions();
      } else if (value === false) {
        this._addManuallyProvidedEmissions();
      }
    });

    this.additionalChangesNotCoveredCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (!value) {
        this._removeAdditionalChangesNotCovered();
      } else {
        this._addAdditionalChangesNotCovered();
      }
    });

    this.siteVisitGroup.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value.type === 'IN_PERSON') {
        this.addInPersonSiteGroup();
      } else if (value.type === 'VIRTUAL') {
        this.addVirtualSiteGroup();
      }
    });
  }

  private _addManuallyProvidedEmissions() {
    if (!this.emissionsGroup.contains('manuallyProvidedEmissions')) {
      this.emissionsGroup.setControl(
        'manuallyProvidedEmissions',
        new FormControl<AviationAerOpinionStatement['manuallyProvidedEmissions'] | null>(
          null,
          manuallyProvidedEmissionsValidators,
        ),
      );

      this.emissionsGroup.value.manuallyProvidedEmissions?.updateValueAndValidity();
    }
  }

  private _removeManuallyProvidedEmissions() {
    if (this.emissionsGroup.contains('manuallyProvidedEmissions')) {
      this.emissionsGroup.removeControl('manuallyProvidedEmissions');
    }
  }

  private _addAdditionalChangesNotCovered() {
    if (!this.changesGroup.contains('additionalChangesNotCoveredDetails')) {
      this.changesGroup.setControl(
        'additionalChangesNotCoveredDetails',
        new FormControl<AviationAerOpinionStatement['additionalChangesNotCoveredDetails'] | null>(
          null,
          additionalChangesNotCoveredDetailsValidators,
        ),
      );
    }
  }

  private _removeAdditionalChangesNotCovered() {
    if (this.changesGroup.contains('additionalChangesNotCoveredDetails')) {
      this.changesGroup.removeControl('additionalChangesNotCoveredDetails');
    }
  }

  private _createVisitDatesGroup(visitDate: AviationAerInPersonSiteVisitDates) {
    const startDate = new Date(visitDate?.startDate);

    return this.fb.group({
      startDate: new FormControl<Date | null>(startDate ?? null, [
        GovukValidators.required('Enter a date when the site visit began'),
        todayOrPastDateValidator(),
      ]),

      numberOfDays: new FormControl<number | null>(visitDate?.numberOfDays ?? null, [
        GovukValidators.required('Enter the number of days your team were on site'),
        GovukValidators.naturalNumber('Must be integer greater than 0'),
      ]),
    });
  }
}
