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

import { FuelInputDataSourcePB } from 'pmrv-api';

import { isProductBenchmark } from '../mmp-sub-installations-status';
import { fuelInputRelevantEmissionFactorAddFormFactory } from './fuel-input-relevant-emission-factor-form.provider';

@Component({
  selector: 'app-fuel-input-relevant-emission-factor',
  templateUrl: './fuel-input-relevant-emission-factor.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [fuelInputRelevantEmissionFactorAddFormFactory],
  styleUrl: './fuel-input-relevant-emission-factor.component.scss',
})
export class FuelInputRelevantEmissionFactorComponent extends ProductBenchmarkComponent implements PendingRequest {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles')?.value),
    map((value) => value?.length > 0),
  );

  fuelInputTypes: FuelInputDataSourcePB['fuelInput'][] = [
    'METHOD_MONITORING_PLAN',
    'LEGAL_METROLOGICAL_CONTROL',
    'OPERATOR_CONTROL_NOT_POINT_B',
    'NOT_OPERATOR_CONTROL_NOT_POINT_B',
    'INDIRECT_DETERMINATION',
    'OTHER_METHODS',
  ];

  weightedEmissionFactorTypes: FuelInputDataSourcePB['weightedEmissionFactor'][] = [
    'CALCULATION_METHOD_MONITORING_PLAN',
    'LABORATORY_ANALYSES_SECTION_61',
    'SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62',
    'CONSTANT_VALUES_STANDARD_SUPPLIER',
    'CONSTANT_VALUES_SCIENTIFIC_EVIDENCE',
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
          fuelInput: new UntypedFormControl(''),
          weightedEmissionFactor: new UntypedFormControl(''),
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
                          fuelInputAndRelevantEmissionFactor: {
                            ...this.form.value,
                            dataSources: this.form.value?.dataSources
                              ? [
                                  ...this.form.value.dataSources.map((dataSource, dataSourceIndex) => {
                                    return {
                                      ...dataSource,
                                      fuelInputDataSourceNo: dataSourceIndex + '',
                                      fuelInput: dataSource.fuelInput === '' ? null : dataSource.fuelInput,
                                      weightedEmissionFactor:
                                        dataSource.weightedEmissionFactor === ''
                                          ? null
                                          : dataSource.weightedEmissionFactor,
                                    };
                                  }),
                                ]
                              : null,
                            fuelInputAndRelevantEmissionFactorType: 'PRODUCT_BENCHMARK',
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
        this.router.navigate(['../imported-exported-measurable-heat'], { relativeTo: this.route });
      });
  }
}
