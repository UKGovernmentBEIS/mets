import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import {
  isFallbackApproach,
  isProductBenchmark,
} from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { GovukValidators } from 'govuk-components';

import { AddPhysicalPartFormProvider } from './add-physical-part-form.provider';

@Component({
  selector: 'app-add-physical-part',
  templateUrl: './add-physical-part.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, SharedPermitModule, RouterLink],
  providers: [AddPhysicalPartFormProvider],
})
export class AddPhysicalPartComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('id') != null));
  id$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('id')));

  subInstallationTypes$ = this.store.getTask('monitoringMethodologyPlans').pipe(
    map((monitoringMethodologyPlans) => {
      const subInstallations = monitoringMethodologyPlans?.digitizedPlan?.subInstallations;
      const productBenchmarks = subInstallations.filter((subInstallation) =>
        isProductBenchmark(subInstallation.subInstallationType),
      );
      const fallbackApproaches = subInstallations.filter((subInstallation) =>
        isFallbackApproach(subInstallation.subInstallationType),
      );

      const completedProductBenchmarks = productBenchmarks.filter(
        (productBenchmark, index) =>
          this.store.getValue().permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark'][index] === true,
      );
      const completedFallbackApproaches = fallbackApproaches.filter(
        (fallbackApproach, index) =>
          this.store.getValue().permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach'][index] === true,
      );

      return [...completedProductBenchmarks, ...completedFallbackApproaches].map(
        (subInstallation) => subInstallation?.subInstallationType,
      );
    }),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onContinue(): void {
    combineLatest([this.permitTask$, this.store, this.isEditing$, this.id$])
      .pipe(
        first(),
        switchMap(([permitTask, state, isEditing, id]) =>
          this.store.patchTask(
            permitTask,
            {
              ...state.permit.monitoringMethodologyPlans,
              digitizedPlan: {
                ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                methodTask: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask,
                  connections: !isEditing
                    ? [
                        ...(state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.connections ?? []),
                        {
                          itemName: this.form.value.itemName,
                          subInstallations: [...this.form.value.subInstallations],
                          itemId:
                            state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.connections?.length > 0
                              ? +state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.connections.reduce(
                                  (max, current) => (current.itemId > max.itemId ? current : max),
                                  state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.connections?.[0],
                                )?.itemId +
                                1 +
                                ''
                              : '0',
                        },
                      ]
                    : state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.connections?.map(
                        (connection) => {
                          if (connection.itemId === id) {
                            return {
                              itemId:
                                state.permit.monitoringMethodologyPlans?.digitizedPlan?.methodTask?.connections?.find(
                                  (conn) => conn.itemId === id,
                                )?.itemId,
                              itemName: this.form.value.itemName,
                              subInstallations: [...this.form.value.subInstallations],
                            };
                          } else return connection;
                        },
                      ),
                },
              },
            },
            false,
            'mmpMethods',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
  }
}

export const atLeastTwoRequiredValidator = (): ValidatorFn => {
  return GovukValidators.builder('Select at least 2 options', (control: UntypedFormControl) => {
    const subInstallations = control;

    return subInstallations.value?.length >= 2 ? null : { atLeastTwoRequired: true };
  });
};
