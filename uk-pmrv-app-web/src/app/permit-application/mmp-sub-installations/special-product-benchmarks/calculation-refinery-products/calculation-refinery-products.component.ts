import { ChangeDetectionStrategy, Component, Inject, ViewChild } from '@angular/core';
import { FormArray, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { isProductBenchmark } from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { WasteGasBalanceEnergyFlowDataSourceDetails } from 'pmrv-api';

import {
  refineryProductsFormFactory,
  refineryProductsRelevantCWTFunctions,
} from './calculation-refinery-products.component-form.provider';

@Component({
  selector: 'app-calculation-refinery-products',
  templateUrl: './calculation-refinery-products.component.html',
  styleUrl: './calculation-refinery-products.component.scss',
  providers: [refineryProductsFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalculationRefineryProductsComponent extends ProductBenchmarkComponent implements PendingRequest {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;

  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles')?.value),
    map((value) => value?.length > 0),
  );

  refineryProductsRelevantCWTFunctions = refineryProductsRelevantCWTFunctions;

  dataSource44Types: WasteGasBalanceEnergyFlowDataSourceDetails['entity'][] = [
    'METHOD_MONITORING_PLAN',
    'LEGAL_METROLOGICAL_CONTROL',
    'OPERATOR_CONTROL_NOT_POINT_B',
    'NOT_OPERATOR_CONTROL_NOT_POINT_B',
    'INDIRECT_DETERMINATION',
    'OTHER_METHODS',
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
    return this.form.get('refineryProductsDataSources') as FormArray;
  }

  addDataSource() {
    this.dataSourcesFormArray.push(
      new UntypedFormGroup(
        {
          ...Object.keys(this.dataSourcesFormArray.value[0]).reduce(
            (accum, key) => ({
              ...accum,
              [key]: new UntypedFormControl(''),
            }),
            {},
          ),
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
                          specialProduct: {
                            ...this.form.value,
                            refineryProductsDataSources:
                              this.form.value?.refineryProductsDataSources?.length > 0
                                ? [
                                    ...this.form.value.refineryProductsDataSources.reduce(
                                      (dataSourceAccum, dataSource, dataSourceIndex) => {
                                        const dataSourceDetails = Object.keys(dataSource).reduce((accum, key) => {
                                          return {
                                            ...accum,
                                            [key]: dataSource[key] === '' ? null : dataSource[key],
                                          };
                                        }, {});

                                        if (Object.keys(dataSourceDetails)?.length > 0) {
                                          dataSourceAccum.push({
                                            dataSourceNo: String(dataSourceIndex),
                                            details: dataSourceDetails,
                                          });
                                          return dataSourceAccum;
                                        }
                                        return dataSourceAccum;
                                      },
                                      [],
                                    ),
                                  ]
                                : null,
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                            specialProductType: 'REFINERY_PRODUCTS',
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
        this.router.navigate(['../summary'], { relativeTo: this.route });
      });
  }
}
