import { ChangeDetectionStrategy, Component, Inject, ViewChild } from '@angular/core';
import { FormArray, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';
import { atLeastOneRequiredNestedValidator } from '@shared-user/utils/validators';

import { MeasurableHeatImported, MeasurableHeatImportedDataSourceDetails } from 'pmrv-api';

import { isFallbackApproach } from '../../mmp-sub-installations-status';
import { measurableHeatImportedAddFormFactory } from './measurable-heat-imported.component-form.provider';

@Component({
  selector: 'app-measurable-heat-imported',
  templateUrl: './measurable-heat-imported.component.html',
  styleUrl: './measurable-heat-imported.component.scss',
  providers: [measurableHeatImportedAddFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurableHeatImportedComponent extends ProductBenchmarkComponent implements PendingRequest {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;

  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles')?.value),
    map((value) => value?.length > 0),
  );

  measurableHeatImportedActivities: MeasurableHeatImported['measurableHeatImportedActivities'] = [
    'MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES',
    'MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK',
    'MEASURABLE_HEAT_IMPORTED_PULP',
    'MEASURABLE_HEAT_IMPORTED_FUEL_BENCHMARK',
    'MEASURABLE_HEAT_IMPORTED_WASTE_GAS',
    'MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED',
  ];

  dataSource45Types: MeasurableHeatImportedDataSourceDetails['entity'][] = [
    'LEGAL_METROLOGICAL_CONTROL_READING',
    'OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'INDIRECT_DETERMINATION_READING',
    'PROXY_CALCULATION_METHOD',
    'OTHER_METHODS',
  ];

  dataSource72Types: MeasurableHeatImportedDataSourceDetails['netContent'][] = [
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
  ) {
    super(store, router, route);
  }

  get dataSourcesFormArray(): FormArray {
    return this.form.get('dataSources') as FormArray;
  }

  addDataSource() {
    this.dataSourcesFormArray.push(
      new UntypedFormGroup(
        {
          ...this.measurableHeatImportedActivities.reduce((accum, activity) => {
            return this.form.value?.measurableHeatImportedActivities?.includes(activity)
              ? {
                  ...accum,
                  ...{
                    [activity]: new UntypedFormGroup({
                      entity: new UntypedFormControl(''),
                      netContent: new UntypedFormControl(''),
                    }),
                  },
                }
              : accum;
          }, {}),
        },
        {
          validators: [
            atLeastOneRequiredNestedValidator(
              'Select at least one option in the data source group or remove the group',
            ),
          ],
        },
      ),
    );
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
                          measurableHeat: {
                            ...item.measurableHeat,
                            measurableHeatImported: {
                              ...this.form.value,
                              dataSources: this.form.value?.dataSources
                                ? [
                                    ...(this.form.value.dataSources as MeasurableHeatImported['dataSources']).reduce(
                                      (dataSourcesFormValuesAccum, dataSource, dataSourceIndex) => {
                                        dataSourcesFormValuesAccum.push({
                                          dataSourceNo: String(dataSourceIndex),
                                          measurableHeatImportedActivityDetails: Object.keys(dataSource).reduce(
                                            (accum, activity) => {
                                              const entity = dataSource?.[activity]?.entity || undefined;
                                              const netContent = dataSource?.[activity]?.netContent || undefined;
                                              if (entity || netContent) {
                                                return {
                                                  ...accum,
                                                  [activity]: {
                                                    entity,
                                                    netContent,
                                                  },
                                                };
                                              }
                                              return accum;
                                            },
                                            {},
                                          ),
                                        });
                                        return dataSourcesFormValuesAccum;
                                      },
                                      [],
                                    ),
                                  ]
                                : null,
                              supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                            },
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
