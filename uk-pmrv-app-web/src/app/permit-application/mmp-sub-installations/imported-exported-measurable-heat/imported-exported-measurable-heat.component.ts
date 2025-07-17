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
import { atLeastOneRequiredValidator } from '@shared/utils/validators';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { ImportedExportedMeasurableHeatEnergyFlowDataSource } from 'pmrv-api';

import { isProductBenchmark } from '../mmp-sub-installations-status';
import { importedExportedMeasurableHeatAddFormFactory } from './imported-exported-measurable-heat-form.provider';

@Component({
  selector: 'app-imported-exported-measurable-heat',
  templateUrl: './imported-exported-measurable-heat.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [importedExportedMeasurableHeatAddFormFactory],
  styleUrl: './imported-exported-measurable-heat.component.scss',
})
export class ImportedExportedMeasurableHeatComponent extends ProductBenchmarkComponent implements PendingRequest {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;

  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles')?.value),
    map((value) => value?.length > 0),
  );

  dataSource45Types: ImportedExportedMeasurableHeatEnergyFlowDataSource['measurableHeatImported'][] = [
    'LEGAL_METROLOGICAL_CONTROL_READING',
    'OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A',
    'INDIRECT_DETERMINATION_READING',
    'PROXY_CALCULATION_METHOD',
    'OTHER_METHODS',
  ];

  dataSource72Types: ImportedExportedMeasurableHeatEnergyFlowDataSource['netMeasurableHeatFlows'][] = [
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
          ...(this.form.value?.fuelBurnCalculationTypes?.includes('MEASURABLE_HEAT_IMPORTED')
            ? {
                measurableHeatImported: new UntypedFormControl(''),
              }
            : {}),
          ...(this.form.value?.fuelBurnCalculationTypes?.includes('MEASURABLE_HEAT_FROM_PULP')
            ? {
                measurableHeatPulp: new UntypedFormControl(''),
              }
            : {}),
          ...(this.form.value?.fuelBurnCalculationTypes?.includes('MEASURABLE_HEAT_FROM_NITRIC_ACID')
            ? {
                measurableHeatNitricAcid: new UntypedFormControl(''),
              }
            : {}),
          ...(this.form.value?.fuelBurnCalculationTypes?.includes('MEASURABLE_HEAT_EXPORTED')
            ? {
                measurableHeatExported: new UntypedFormControl(''),
              }
            : {}),
          netMeasurableHeatFlows: new UntypedFormControl(''),
        },
        {
          validators: atLeastOneRequiredValidator(
            'Select at least one option in the data source group or remove the group',
          ),
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
          const productBenchmarkIndex = subInstallations
            ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
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
                          importedExportedMeasurableHeat: {
                            ...this.form.value,
                            dataSources: this.form.value?.dataSources
                              ? [
                                  ...this.form.value.dataSources.map((dataSource, dataSourceIndex) => {
                                    return {
                                      ...dataSource,
                                      energyFlowDataSourceNo: dataSourceIndex + '',
                                      measurableHeatImported:
                                        dataSource.measurableHeatImported === ''
                                          ? null
                                          : dataSource.measurableHeatImported,
                                      measurableHeatPulp:
                                        dataSource.measurableHeatPulp === '' ? null : dataSource.measurableHeatPulp,
                                      measurableHeatNitricAcid:
                                        dataSource.measurableHeatNitricAcid === ''
                                          ? null
                                          : dataSource.measurableHeatNitricAcid,
                                      measurableHeatExported:
                                        dataSource.measurableHeatExported === ''
                                          ? null
                                          : dataSource.measurableHeatExported,
                                      netMeasurableHeatFlows:
                                        dataSource.netMeasurableHeatFlows === ''
                                          ? null
                                          : dataSource.netMeasurableHeatFlows,
                                    };
                                  }),
                                ]
                              : null,
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                          },
                        }
                      : {
                          ...item,
                        },
                  ),
                },
              },
              productBenchmarkIndex !== -1 &&
                state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark']?.length
                ? state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark']?.map((item, idx) =>
                    productBenchmarkIndex === idx ? false : item,
                  )
                : state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark']
                  ? [...state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark'], false]
                  : [false],
              'MMP_SUB_INSTALLATION_Product_Benchmark',
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
        this.router.navigate(['../waste-gas-balance'], { relativeTo: this.route });
      });
  }
}
