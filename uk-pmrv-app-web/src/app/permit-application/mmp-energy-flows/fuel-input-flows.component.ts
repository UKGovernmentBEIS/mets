import { ChangeDetectionStrategy, Component, Inject, ViewChild } from '@angular/core';
import { FormArray, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { IncludeAnswerDetailsComponent } from '@permit-application/mmp-sub-installations/shared/include-answer-details.component';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { FuelInputDataSource } from 'pmrv-api';

import { fuelInputFlowsAddFormFactory } from './fuel-input-flows-form.provider';

@Component({
  selector: 'app-fuel-input-flows',
  templateUrl: './fuel-input-flows.component.html',
  standalone: true,
  imports: [SharedModule, SharedPermitModule, IncludeAnswerDetailsComponent],
  providers: [fuelInputFlowsAddFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './fuel-input-flows.component.scss',
})
export class FuelInputFlowsComponent implements PendingRequest {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  fuelInputTypes: FuelInputDataSource['fuelInput'][] = [
    'METHOD_MONITORING_PLAN',
    'LEGAL_METROLOGICAL_CONTROL',
    'OPERATOR_CONTROL_NOT_POINT_B',
    'NOT_OPERATOR_CONTROL_NOT_POINT_B',
    'INDIRECT_DETERMINATION',
    'OTHER_METHODS',
  ];

  energyContentTypes: FuelInputDataSource['energyContent'][] = [
    'CALCULATION_METHOD_MONITORING_PLAN',
    'LABORATORY_ANALYSES_SECTION_61',
    'SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62',
    'CONSTANT_VALUES_STANDARD_SUPPLIER',
    'CONSTANT_VALUES_SCIENTIFIC_EVIDENCE',
  ];

  get dataSourcesFormArray(): FormArray {
    return this.form.get('fuelInputDataSources') as FormArray;
  }

  addDataSource() {
    this.dataSourcesFormArray.push(
      new UntypedFormGroup(
        {
          fuelInput: new UntypedFormControl(''),
          energyContent: new UntypedFormControl(''),
        },
        {
          validators: [
            atLeastOneRequiredValidator('Select at least one option in the data source group or remove the group'),
          ],
        },
      ),
    );
  }

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }

  onSubmit(): void {
    combineLatest([this.permitTask$, this.store])
      .pipe(
        first(),
        switchMap(([permitTask, state]) => {
          return this.store
            .patchTask(
              permitTask,
              {
                ...state.permit.monitoringMethodologyPlans,
                digitizedPlan: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                  energyFlows: {
                    ...state.permit.monitoringMethodologyPlans?.digitizedPlan?.energyFlows,
                    fuelInputFlows: {
                      ...this.form.value,

                      fuelInputDataSources: [
                        ...this.form.value.fuelInputDataSources.map((dataSource, dataSourceIndex) => {
                          return {
                            ...dataSource,
                            fuelInput: dataSource.fuelInput === '' ? null : dataSource.fuelInput,
                            energyContent: dataSource.energyContent === '' ? null : dataSource.energyContent,
                            dataSourceNumber: String(dataSourceIndex),
                          };
                        }),
                      ],
                      supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                    },
                  },
                },
              },
              false,
              'mmpEnergyFlows',
            )
            .pipe(this.pendingRequest.trackRequest());
        }),
        first(),
      )
      .subscribe(() => {
        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...this.form.value?.supportingFiles?.reduce(
              (result, item) => ({ ...result, [item.uuid]: item.file.name }),
              {},
            ),
          },
        });

        this.router.navigate(['./measurable-heat-flows'], { relativeTo: this.route });
      });
  }
}
