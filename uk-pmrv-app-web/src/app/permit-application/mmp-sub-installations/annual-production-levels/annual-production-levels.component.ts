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

import { QuantityProductDataSource } from 'pmrv-api';

import { exchangeabilityTypes, isProductBenchmark } from '../mmp-sub-installations-status';
import { annualProductionLevelsAddFormFactory } from './annual-production-levels-form.provider';

@Component({
  selector: 'app-annual-production-levels',
  templateUrl: './annual-production-levels.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [annualProductionLevelsAddFormFactory],
  styleUrl: './annual-production-levels.component.scss',
})
export class AnnualProductionLevelComponent extends ProductBenchmarkComponent implements PendingRequest {
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  quantityProductTypes: QuantityProductDataSource['quantityProduct'][] = [
    'METHOD_MONITORING_PLAN',
    'LEGAL_METROLOGICAL_CONTROL',
    'OPERATOR_CONTROL_NOT_POINT_B',
    'NOT_OPERATOR_CONTROL_NOT_POINT_B',
    'INDIRECT_DETERMINATION',
    'OTHER_METHODS',
  ];

  get quantityProductDataSourcesFormArray(): FormArray {
    return this.form.get('quantityProductDataSources') as FormArray;
  }

  addQuantityProductDataSource() {
    this.quantityProductDataSourcesFormArray.push(
      new UntypedFormGroup({
        quantityProduct: new UntypedFormControl('', [GovukValidators.required('Select the data sources used')]),
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
                          annualLevel: {
                            ...this.form.value,
                            annualLevelType: 'PRODUCTION',
                            quantityProductDataSources: [
                              ...this.form.value.quantityProductDataSources.map((dataSource, dataSourceIndex) => {
                                return { ...dataSource, quantityProductDataSourceNo: dataSourceIndex + '' };
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
        first(),
        switchMap(() => this.productBenchmarkType$),
      )
      .subscribe((productBenchmarkType) => {
        const nextStep = exchangeabilityTypes.includes(productBenchmarkType)
          ? 'exchangeability'
          : 'imported-measureable-heat-flows';

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
        this.router.navigate([`../${nextStep}`], { relativeTo: this.route });
      });
  }
}
