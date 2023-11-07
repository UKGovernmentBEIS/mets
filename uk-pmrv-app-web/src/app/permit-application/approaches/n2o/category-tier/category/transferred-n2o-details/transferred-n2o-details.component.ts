import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormGroup, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { MeasurementOfN2OEmissionPointCategoryAppliedTier, MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { transferredN2ODetailsFormProvider } from './transferred-n2o-details-form.provider';

@Component({
  selector: 'app-transferred-n2o-details',
  templateUrl: './transferred-n2o-details.component.html',
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [transferredN2ODetailsFormProvider],
})
export class TransferredN2ODetailsComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  emissionPointCategory$ = this.index$.pipe(
    switchMap((index) =>
      this.store.findTask('monitoringApproaches.MEASUREMENT_N2O').pipe(
        map((response) => {
          return response?.emissionPointCategoryAppliedTiers?.[index]?.emissionPointCategory;
        }),
      ),
    ),
  );

  form: UntypedFormGroup;

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(form: FormGroup): void {
    this.form = form;

    combineLatest([
      this.index$,
      this.store.findTask<MeasurementOfN2OEmissionPointCategoryAppliedTier[]>(
        'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
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
                MEASUREMENT_N2O: {
                  ...state.permit.monitoringApproaches.MEASUREMENT_N2O,
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
                } as MeasurementOfN2OMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              MEASUREMENT_N2O_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.MEASUREMENT_N2O_Category.map((item, idx) =>
                      index === idx ? true : item,
                    )
                  : [...(state.permitSectionsCompleted.MEASUREMENT_N2O_Category ?? []), true],
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
