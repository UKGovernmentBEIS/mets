import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormArray, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { GovukValidators } from 'govuk-components';

import { FuelAndElectricityExchangeabilityEnergyFlowDataSource } from 'pmrv-api';

import { isProductBenchmark } from '../mmp-sub-installations-status';
import { exchangeabilityAddFormFactory } from './exchangeability-form.provider';

@Component({
  selector: 'app-exchangeability',
  templateUrl: './exchangeability.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [exchangeabilityAddFormFactory],
  styleUrl: './exchangeability.component.scss',
})
export class ExchangeabilityComponent extends ProductBenchmarkComponent implements PendingRequest {
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  relevantElectricityConsumptionTypes: FuelAndElectricityExchangeabilityEnergyFlowDataSource['relevantElectricityConsumption'][] =
    [
      'LEGAL_METROLOGICAL_CONTROL_READING',
      'OPERATOR_CONTROL_DIRECT_READING_NOT_A',
      'NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A',
      'INDIRECT_DETERMINATION_READING',
      'PROXY_CALCULATION_METHOD',
      'OTHER_METHODS',
    ];

  get energyFlowDataSourcesFormArray(): FormArray {
    return this.form.get('fuelAndElectricityExchangeabilityEnergyFlowDataSources') as FormArray;
  }

  addElectricityConsumptionDataSource() {
    this.energyFlowDataSourcesFormArray.push(
      new UntypedFormGroup({
        relevantElectricityConsumption: new UntypedFormControl('', [
          GovukValidators.required('Select the data sources used'),
        ]),
      }),
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
                          fuelAndElectricityExchangeability: {
                            ...this.form.value,
                            fuelAndElectricityExchangeabilityEnergyFlowDataSources: [
                              ...this.form.value.fuelAndElectricityExchangeabilityEnergyFlowDataSources.map(
                                (dataSource, dataSourceIndex) => {
                                  return { ...dataSource, energyFlowDataSourceNo: dataSourceIndex + '' };
                                },
                              ),
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
        this.router.navigate(['../imported-measureable-heat-flows'], { relativeTo: this.route });
      });
  }
}
