import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { CalculationOfCO2MonitoringApproach, CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

@Component({
  selector: 'app-transferred-co2-details',
  templateUrl: './transferred-co2-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferredCo2DetailsComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  sourceStreamCategory$ = this.index$.pipe(
    switchMap((index) =>
      this.store.findTask('monitoringApproaches.CALCULATION_CO2').pipe(
        map((response) => {
          return response?.sourceStreamCategoryAppliedTiers?.[index]?.sourceStreamCategory;
        }),
      ),
    ),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(form: UntypedFormGroup): void {
    combineLatest([
      this.index$,
      this.store.findTask<CalculationSourceStreamCategoryAppliedTier[]>(
        'monitoringApproaches.CALCULATION_CO2.sourceStreamCategoryAppliedTiers',
      ),
      this.store,
      this.route.data,
    ])
      .pipe(
        first(),
        switchMap(([index, tiers, state, data]) => {
          return this.store.postCategoryTask(data.taskKey, {
            ...state,
            permit: {
              ...state.permit,
              monitoringApproaches: {
                ...state.permit.monitoringApproaches,
                CALCULATION_CO2: {
                  ...state.permit.monitoringApproaches.CALCULATION_CO2,
                  sourceStreamCategoryAppliedTiers: tiers.map((tier, idx) =>
                    index === idx
                      ? {
                          ...tier,
                          sourceStreamCategory: {
                            ...tier.sourceStreamCategory,
                            transfer: {
                              ...tier.sourceStreamCategory.transfer,
                              installationDetailsType: form.controls.installationDetailsType.value,
                              ...(form.controls.installationDetailsType.value === 'INSTALLATION_EMITTER'
                                ? {
                                    installationEmitter: {
                                      emitterId: form.controls.emitterId.value,
                                      email: form.controls.email.value,
                                    },
                                  }
                                : {
                                    installationEmitter: null,
                                  }),
                              ...(form.controls.installationDetailsType.value === 'INSTALLATION_DETAILS'
                                ? {
                                    installationDetails: {
                                      city: form.controls.city.value,
                                      email: form.controls.email2.value,
                                      installationName: form.controls.installationName.value,
                                      line1: form.controls.line1.value,
                                      line2: form.controls?.line2.value,
                                      postcode: form.controls.postcode.value,
                                    },
                                  }
                                : {
                                    installationDetails: null,
                                  }),
                            },
                          },
                        }
                      : tier,
                  ),
                } as CalculationOfCO2MonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              CALCULATION_CO2_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.CALCULATION_CO2_Category.map((item, idx) =>
                      index === idx ? true : item,
                    )
                  : [...(state.permitSectionsCompleted.CALCULATION_CO2_Category ?? []), true],
            },
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
