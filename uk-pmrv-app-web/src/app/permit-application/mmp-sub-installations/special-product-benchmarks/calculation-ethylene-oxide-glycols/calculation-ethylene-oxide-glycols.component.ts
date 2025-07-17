import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
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

import { calculationEthyleneOxideGlycolsAddFormFactory } from './calculation-ethylene-oxide-glycols-form.provider';

@Component({
  selector: 'app-calculation-ethylene-oxide-glycols',
  templateUrl: './calculation-ethylene-oxide-glycols.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [calculationEthyleneOxideGlycolsAddFormFactory],
})
export class CalculationEthyleneOxideGlycolsComponent extends ProductBenchmarkComponent implements PendingRequest {
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  get dataSourcesFormArray(): FormArray {
    return this.form.get('dataSources') as FormArray;
  }

  removeDataSource(index: number) {
    this.dataSourcesFormArray.removeAt(index);
  }

  addDataSource() {
    this.dataSourcesFormArray.push(
      new UntypedFormGroup({
        details: new UntypedFormGroup(
          {
            ETHYLEN_OXIDE: new UntypedFormControl(''),
            MONOTHYLENE_GLYCOL: new UntypedFormControl(''),
            DIETHYLENE_GLYCOL: new UntypedFormControl(''),
            TRIETHYLENE_GLYCOL: new UntypedFormControl(''),
          },
          {
            validators: [
              atLeastOneRequiredValidator('Select at least one option in the data source group or remove the group'),
            ],
          },
        ),
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
                          specialProduct: {
                            ...this.form.value,
                            dataSources: [
                              ...this.form.value.dataSources.map((dataSource, dataSourceIndex) => {
                                // The backend needs nulls instead of zero space to keep the existence of selection
                                const details = Object.fromEntries(
                                  Object.entries(dataSource.details).map(([key, value]) => [
                                    key,
                                    value === '' ? null : value,
                                  ]),
                                );

                                return {
                                  ...dataSource,
                                  details,
                                  dataSourceNo: dataSourceIndex.toString(),
                                };
                              }),
                            ],
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                            specialProductType: 'ETHYLENE_OXIDE_ETHYLENE_GLYCOLS',
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
