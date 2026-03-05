import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { MeasurementOfCO2EmissionPointCategoryAppliedTier, MeasurementOfCO2MonitoringApproach } from 'pmrv-api';

@Component({
  selector: 'app-transferred-co2-details',
  templateUrl: './transferred-co2-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferredCo2DetailsComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  emissionPointCategory$ = this.index$.pipe(
    switchMap((index) =>
      this.store.findTask('monitoringApproaches.MEASUREMENT_CO2').pipe(
        map((response) => {
          return response?.emissionPointCategoryAppliedTiers?.[index]?.emissionPointCategory;
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
      this.store.findTask<MeasurementOfCO2EmissionPointCategoryAppliedTier[]>(
        'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
      ),
      this.store,
      this.route.data,
    ])
      .pipe(
        first(),
        switchMap(([index, tiers, state, data]) =>
          this.store.postCategoryTask(data.taskKey, {
            ...state,
            permit: {
              ...state.permit,
              monitoringApproaches: {
                ...state.permit.monitoringApproaches,
                MEASUREMENT_CO2: {
                  ...state.permit.monitoringApproaches.MEASUREMENT_CO2,
                  emissionPointCategoryAppliedTiers: tiers.map((tier, idx) =>
                    index === idx
                      ? {
                          ...tier,
                          emissionPointCategory: {
                            ...tier.emissionPointCategory,
                            transfer: {
                              ...tier.emissionPointCategory.transfer,
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
                } as MeasurementOfCO2MonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              MEASUREMENT_CO2_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.MEASUREMENT_CO2_Category.map((item, idx) =>
                      index === idx ? true : item,
                    )
                  : [...(state.permitSectionsCompleted.MEASUREMENT_CO2_Category ?? []), true],
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
