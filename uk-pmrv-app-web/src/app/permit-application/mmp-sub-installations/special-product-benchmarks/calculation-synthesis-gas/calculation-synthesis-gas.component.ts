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

import { calculationSynthesisGasAddFormFactory } from './calculation-synthesis-gas-form.provider';

@Component({
  selector: 'app-calculation-synthesis-gas',
  templateUrl: './calculation-synthesis-gas.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [calculationSynthesisGasAddFormFactory],
})
export class CalculationSynthesisGasComponent extends ProductBenchmarkComponent implements PendingRequest {
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
            SYNTHESIS_GAS_TOTAL_PRODUCTION: new UntypedFormControl(''),
            SYNTHESIS_GAS_COMPOSITION_DATA: new UntypedFormControl(''),
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
                                return {
                                  ...dataSource,
                                  dataSourceNo: String(dataSourceIndex),
                                  details: {
                                    SYNTHESIS_GAS_TOTAL_PRODUCTION:
                                      dataSource.details.SYNTHESIS_GAS_TOTAL_PRODUCTION === ''
                                        ? null
                                        : dataSource.details.SYNTHESIS_GAS_TOTAL_PRODUCTION,
                                    SYNTHESIS_GAS_COMPOSITION_DATA:
                                      dataSource.details.SYNTHESIS_GAS_COMPOSITION_DATA === ''
                                        ? null
                                        : dataSource.details.SYNTHESIS_GAS_COMPOSITION_DATA,
                                  },
                                };
                              }),
                            ],
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                            specialProductType: 'SYNTHESIS_GAS',
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
