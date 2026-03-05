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

import { MeasurableHeatFlowsDataSource } from 'pmrv-api';

import { measurableHeatFlowsAddFormFactory } from './measurable-heat-flows-form.provider';

@Component({
  selector: 'app-measurable-heat-flows',
  templateUrl: './measurable-heat-flows.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, SharedPermitModule, IncludeAnswerDetailsComponent],
  providers: [measurableHeatFlowsAddFormFactory],
  styleUrl: './measurable-heat-flows.component.scss',
})
export class MeasurableHeatFlowsComponent implements PendingRequest {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;

  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles')?.value),
    map((value) => value?.length > 0),
  );

  dataSource45Types: MeasurableHeatFlowsDataSource['quantification'][] = [
    'LEGAL_METROLOGICAL_CONTROL_READING',
    'OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'INDIRECT_DETERMINATION_READING',
    'PROXY_CALCULATION_METHOD',
    'OTHER_METHODS',
  ];

  dataSource72Types: MeasurableHeatFlowsDataSource['net'][] = [
    'MEASUREMENTS',
    'DOCUMENTATION',
    'PROXY_MEASURED_EFFICIENCY',
    'PROXY_REFERENCE_EFFICIENCY',
  ];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  get dataSourcesFormArray(): FormArray {
    return this.form.get('measurableHeatFlowsDataSources') as FormArray;
  }

  addDataSource() {
    this.dataSourcesFormArray.push(
      new UntypedFormGroup(
        {
          quantification: new UntypedFormControl(''),
          net: new UntypedFormControl(''),
        },
        {
          validators: [
            atLeastOneRequiredValidator('Select at least one option in the data source group or remove the group'),
          ],
        },
      ),
    );
  }

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
                    measurableHeatFlows: {
                      ...this.form.value,
                      measurableHeatFlowsDataSources: this.form.value?.measurableHeatFlowsDataSources
                        ? [
                            ...this.form.value.measurableHeatFlowsDataSources.map((dataSource, dataSourceIndex) => {
                              return {
                                ...dataSource,
                                net: dataSource.net === '' ? null : dataSource.net,
                                quantification: dataSource.quantification === '' ? null : dataSource.quantification,
                                dataSourceNumber: String(dataSourceIndex),
                              };
                            }),
                          ]
                        : null,
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

        this.router.navigate(['../waste-gas-flows'], { relativeTo: this.route });
      });
  }
}
