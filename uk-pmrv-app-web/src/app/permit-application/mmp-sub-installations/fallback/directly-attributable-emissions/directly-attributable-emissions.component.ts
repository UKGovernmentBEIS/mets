import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { isFallbackApproach } from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { directlyAttributableEmissionsAddFormFactory } from './directly-attributable-emissions-form.provider';

@Component({
  selector: 'app-directly-attributable-emissions-fa',
  templateUrl: './directly-attributable-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [directlyAttributableEmissionsAddFormFactory],
  styleUrl: './directly-attributable-emissions.component.scss',
})
export class DirectlyAttributableEmissionsFAComponent extends ProductBenchmarkComponent implements PendingRequest {
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  heatBenchmark = ['HEAT_BENCHMARK_CL', 'HEAT_BENCHMARK_NON_CL', 'DISTRICT_HEATING_NON_CL'];

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles')?.valueChanges?.pipe(
    startWith(this.form.get('supportingFiles')?.value),
    map((value) => value?.length > 0),
  );

  heatEmissions = ['HEAT_BENCHMARK_CL', 'HEAT_BENCHMARK_NON_CL', 'DISTRICT_HEATING_NON_CL'];

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
                          directlyAttributableEmissions: {
                            ...this.form.value,
                            supportingFiles: this.form.value?.supportingFiles?.map((file) => file.uuid),
                            directlyAttributableEmissionsType: 'FALLBACK_APPROACH',
                          },
                        }
                      : {
                          ...item,
                        },
                  ),
                },
              },
              productBenchmarkIndex !== -1 &&
                state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach']?.length
                ? state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach']?.map((item, idx) =>
                    productBenchmarkIndex === idx ? false : item,
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

        this.router.navigate(['../fuel-input-relevant-emission-factor'], { relativeTo: this.route });
      });
  }
}
