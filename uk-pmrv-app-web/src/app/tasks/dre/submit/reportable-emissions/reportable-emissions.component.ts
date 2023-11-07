import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, startWith, switchMap } from 'rxjs';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DreService } from '../../core/dre.service';
import {
  calculateTotalReportableEmissionsAmount,
  calculateTotalSustainableBiomassAmount,
} from '../../core/reportable.emissions';
import { reportableEmissionsFormProvider } from './reportable-emissions-form.provider';

@Component({
  selector: 'app-reportable-emissions',
  templateUrl: './reportable-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reportableEmissionsFormProvider],
})
export class ReportableEmissionsComponent {
  private readonly nextWizardStep = 'information-sources';
  readonly emissionSuffixLabel = 'tonnes CO2e';
  monitoringApproaches$ = this.dreService.dre$.pipe(map((dre) => dre.monitoringApproachReportingEmissions));

  totalReportableEmissions$: Observable<string> = this.form.valueChanges.pipe(
    startWith(this.form.value),
    map((formData) =>
      calculateTotalReportableEmissionsAmount({
        CALCULATION_CO2: formData.calculationOfCO2,
        MEASUREMENT_CO2: formData.measurementOfCO2,
        MEASUREMENT_N2O: formData.measurementOfN2O,
        CALCULATION_PFC: formData.calculationOfPFC,
        INHERENT_CO2: formData.inherentOfCO2,
        FALLBACK: formData.fallback,
      }),
    ),
  );

  totalSustainableBiomassEmissions$: Observable<string> = this.form.valueChanges.pipe(
    startWith(this.form.value),
    map((formData) =>
      calculateTotalSustainableBiomassAmount({
        CALCULATION_CO2: formData.calculationOfCO2,
        MEASUREMENT_CO2: formData.measurementOfCO2,
        MEASUREMENT_N2O: formData.measurementOfN2O,
        CALCULATION_PFC: formData.calculationOfPFC,
        INHERENT_CO2: formData.inherentOfCO2,
        FALLBACK: formData.fallback,
      }),
    ),
  );

  constructor(
    @Inject(DRE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly dreService: DreService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
    } else {
      this.monitoringApproaches$
        .pipe(
          first(),
          switchMap((monitoringApproaches) =>
            this.dreService.saveDre(
              {
                monitoringApproachReportingEmissions: {
                  ...(monitoringApproaches.CALCULATION_CO2
                    ? {
                        CALCULATION_CO2: {
                          ...monitoringApproaches.CALCULATION_CO2,
                          ...this.form.value.calculationOfCO2,
                        },
                      }
                    : {}),
                  ...(monitoringApproaches.MEASUREMENT_CO2
                    ? {
                        MEASUREMENT_CO2: {
                          ...monitoringApproaches.MEASUREMENT_CO2,
                          ...this.form.value.measurementOfCO2,
                        },
                      }
                    : {}),
                  ...(monitoringApproaches.MEASUREMENT_N2O
                    ? {
                        MEASUREMENT_N2O: {
                          ...monitoringApproaches.MEASUREMENT_N2O,
                          ...this.form.value.measurementOfN2O,
                        },
                      }
                    : {}),
                  ...(monitoringApproaches.CALCULATION_PFC
                    ? {
                        CALCULATION_PFC: {
                          ...monitoringApproaches.CALCULATION_PFC,
                          ...this.form.value.calculationOfPFC,
                        },
                      }
                    : {}),
                  ...(monitoringApproaches.INHERENT_CO2
                    ? {
                        INHERENT_CO2: {
                          ...monitoringApproaches.INHERENT_CO2,
                          ...this.form.value.inherentOfCO2,
                        },
                      }
                    : {}),
                  ...(monitoringApproaches.FALLBACK
                    ? {
                        FALLBACK: {
                          ...monitoringApproaches.FALLBACK,
                          ...this.form.value.fallback,
                        },
                      }
                    : {}),
                },
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route }));
    }
  }
}
