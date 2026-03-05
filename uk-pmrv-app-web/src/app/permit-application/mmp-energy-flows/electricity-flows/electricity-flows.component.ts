import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormArray, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { IncludeAnswerDetailsComponent } from '@permit-application/mmp-sub-installations/shared/include-answer-details.component';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { GovukValidators } from 'govuk-components';

import { ElectricityFlowsDataSource } from 'pmrv-api';

import { electricityFlowsAddFormFactory } from './electricity-flows-form.provider';

@Component({
  selector: 'app-electricity-flows',
  templateUrl: './electricity-flows.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [electricityFlowsAddFormFactory],
  standalone: true,
  imports: [SharedPermitModule, SharedModule, IncludeAnswerDetailsComponent, RouterModule],
  styleUrl: './electricity-flows.component.scss',
})
export class ElectricityFlowsComponent implements PendingRequest {
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles')?.value),
    map((value) => value?.length > 0),
  );

  dataSource45Types: ElectricityFlowsDataSource['quantification'][] = [
    'LEGAL_METROLOGICAL_CONTROL_READING',
    'OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'INDIRECT_DETERMINATION_READING',
    'PROXY_CALCULATION_METHOD',
    'OTHER_METHODS',
  ];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  get dataSourcesFormArray(): FormArray {
    return this.form.get('electricityFlowsDataSources') as FormArray;
  }

  addDataSource() {
    this.dataSourcesFormArray.push(
      new UntypedFormGroup({
        quantification: new UntypedFormControl('', [
          GovukValidators.required('Select the data source for quantification of energy flows'),
        ]),
      }),
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
                    electricityFlows: {
                      ...this.form.value,
                      electricityFlowsDataSources: this.form.value?.electricityFlowsDataSources
                        ? [
                            ...this.form.value.electricityFlowsDataSources.map((dataSource, dataSourceIndex) => {
                              return {
                                ...dataSource,
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

        this.router.navigate(['../summary'], { relativeTo: this.route });
      });
  }
}
