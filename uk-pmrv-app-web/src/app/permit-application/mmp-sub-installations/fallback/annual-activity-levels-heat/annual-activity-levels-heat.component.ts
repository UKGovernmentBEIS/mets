import { ChangeDetectionStrategy, Component, Inject, ViewChild } from '@angular/core';
import { FormArray, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { isFallbackApproach } from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { MeasurableHeatFlow } from 'pmrv-api';

import { annualActivityHeatAddFormFactory } from './annual-activity-levels-heat-form.provider';

@Component({
  selector: 'app-annual-activity-levels-heat',
  templateUrl: './annual-activity-levels-heat.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [annualActivityHeatAddFormFactory],
  styleUrl: './annual-activity-levels-heat.component.scss',
})
export class AnnualActivityLevelsHeatComponent extends ProductBenchmarkComponent implements PendingRequest {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;

  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  processEmissions = ['PROCESS_EMISSIONS_CL', 'PROCESS_EMISSIONS_NON_CL'] as any;

  quantificationTypes: MeasurableHeatFlow['quantification'][] = [
    'LEGAL_METROLOGICAL_CONTROL_READING',
    'OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'INDIRECT_DETERMINATION_READING',
    'PROXY_CALCULATION_METHOD',
    'OTHER_METHODS',
  ];

  netTypes: MeasurableHeatFlow['net'][] = [
    'MEASUREMENTS',
    'DOCUMENTATION',
    'PROXY_MEASURED_EFFICIENCY',
    'PROXY_REFERENCE_EFFICIENCY',
  ];

  get measurableHeatFlowDataSourcesFormArray(): FormArray {
    return this.form.get('measurableHeatFlowList') as FormArray;
  }

  addMeasureableHeatDataSource() {
    this.measurableHeatFlowDataSourcesFormArray.push(
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
  ) {
    super(store, router, route);
  }

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }

  onSubmit(): void {
    combineLatest([this.permitTask$, this.store, this.route.paramMap])
      .pipe(
        first(),
        switchMap(([permitTask, state, paramMap]) => {
          const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
          const subInstallations = digitizedPlan && digitizedPlan?.subInstallations;

          const fallbackApproachIndex = subInstallations
            ?.filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType))
            .findIndex((x) => x.subInstallationNo == paramMap.get('subInstallationNo'));

          return this.store
            .patchTask(
              permitTask,
              {
                ...state.permit.monitoringMethodologyPlans,
                digitizedPlan: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                  subInstallations: subInstallations?.map((item) =>
                    item.subInstallationNo === paramMap.get('subInstallationNo')
                      ? {
                          ...item,
                          annualLevel: {
                            ...this.form.value,
                            annualLevelType: 'ACTIVITY_HEAT',
                            measurableHeatFlowList: [
                              ...this.form.value.measurableHeatFlowList.map((dataSource, dataSourceIndex) => {
                                return {
                                  ...dataSource,
                                  quantification: dataSource?.quantification === '' ? null : dataSource.quantification,
                                  net: dataSource?.net === '' ? null : dataSource.net,
                                  measurableHeatFlowQuantificationNo: dataSourceIndex + '',
                                };
                              }),
                            ],
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                          },
                        }
                      : {
                          ...item,
                        },
                  ),
                },
              },
              fallbackApproachIndex !== -1 &&
                state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach']?.length
                ? state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach']?.map((item, idx) =>
                    fallbackApproachIndex === idx ? false : item,
                  )
                : state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach']
                  ? [...state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach'], false]
                  : [false],
              'MMP_SUB_INSTALLATION_Fallback_Approach',
            )
            .pipe(this.pendingRequest.trackRequest());
        }),
        first(),
        switchMap(() => combineLatest([this.productBenchmarkType$])),
      )
      .subscribe(([productBenchmarkType]) => {
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

        let nextStep = `../directly-attributable-emissions`;

        if (this.processEmissions.includes(productBenchmarkType)) {
          nextStep = '../../../';
        }

        this.router.navigate([`./${nextStep}`], { relativeTo: this.route });
      });
  }
}
