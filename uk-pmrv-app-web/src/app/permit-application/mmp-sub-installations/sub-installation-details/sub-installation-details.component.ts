import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { exchangeabilityTypes, isProductBenchmark, productBenchmarkTypes } from '../mmp-sub-installations-status';
import { productBenchmarkAddFormFactory } from './product-benchmark-details-form.provider';

@Component({
  selector: 'app-sub-installation-details',
  templateUrl: './sub-installation-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [productBenchmarkAddFormFactory],
})
export class SubInstallationDetailsComponent extends ProductBenchmarkComponent implements PendingRequest {
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  productBenchmarkTypes$ = this.store.getTask('monitoringMethodologyPlans').pipe(
    map((monitoringMethodologyPlans) => {
      let usedTypes = monitoringMethodologyPlans?.digitizedPlan?.subInstallations
        ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
        .map((productBenchmark) => productBenchmark.subInstallationType);

      if (this.form.value?.subInstallationType) {
        usedTypes = usedTypes.filter((item) => item !== this.form.value?.subInstallationType);
      }

      return productBenchmarkTypes.filter((element) => !usedTypes?.includes(element));
    }),
  );
  subInstallationNo: string;

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles').valueChanges.pipe(
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

          this.subInstallationNo =
            paramMap?.get('subInstallationNo') != null
              ? paramMap?.get('subInstallationNo')
              : subInstallations
                ? subInstallations?.length + ''
                : 0 + '';

          return this.store
            .patchTask(
              permitTask,
              {
                ...state.permit.monitoringMethodologyPlans,
                digitizedPlan: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                  subInstallations: subInstallations?.some(
                    (item) => item.subInstallationNo === paramMap.get('subInstallationNo'),
                  )
                    ? subInstallations?.map((item, index) => {
                        if (
                          item.subInstallationNo === paramMap.get('subInstallationNo') &&
                          item?.specialProduct &&
                          item?.specialProduct?.specialProductType !== this.form.value?.subInstallationType
                        ) {
                          delete item?.specialProduct;
                        }

                        if (
                          item.subInstallationNo === paramMap.get('subInstallationNo') &&
                          !exchangeabilityTypes.includes(this.form.value?.subInstallationType)
                        ) {
                          delete item?.fuelAndElectricityExchangeability;
                        }

                        return item.subInstallationNo === paramMap.get('subInstallationNo')
                          ? {
                              ...item,
                              ...this.form.value,
                              supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                              subInstallationNo: paramMap.get('subInstallationNo'),
                            }
                          : {
                              ...item,
                              subInstallationNo: '' + index,
                            };
                      })
                    : subInstallations && subInstallations?.length
                      ? [
                          ...subInstallations.map((item, index) => {
                            return { ...item, subInstallationNo: '' + index };
                          }),
                          {
                            ...this.form.value,
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                            subInstallationNo: (subInstallations?.length ?? 0) + '',
                          },
                        ]
                      : [
                          {
                            ...this.form.value,
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                            subInstallationNo: (subInstallations?.length ?? 0) + '',
                          },
                        ],
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

        this.router.navigate(['../', this.subInstallationNo, 'annual-production-level'], { relativeTo: this.route });
      });
  }
}
