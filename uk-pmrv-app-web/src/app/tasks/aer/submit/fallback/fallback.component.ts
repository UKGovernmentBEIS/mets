import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { buildTaskData, calculateFallbackReportableEmissions } from '@tasks/aer/submit/fallback/fallback';
import { fallbackFormProvider } from '@tasks/aer/submit/fallback/fallback-form.provider';

import { AerApplicationSubmitRequestTaskPayload, FallbackBiomass, FallbackEmissions } from 'pmrv-api';

@Component({
  selector: 'app-fallback',
  templateUrl: './fallback.component.html',
  providers: [fallbackFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FallbackComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = 'description';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      (this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>)
        .pipe(
          first(),
          switchMap((payload) => {
            const emissions = payload.aer?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions;
            const biomass: FallbackBiomass =
              this.form.controls.contains.value === true
                ? {
                    contains: this.form.controls.contains.value,
                    totalSustainableBiomassEmissions: emissions?.biomass?.totalSustainableBiomassEmissions,
                    totalNonSustainableBiomassEmissions: emissions?.biomass?.totalNonSustainableBiomassEmissions,
                    totalEnergyContentFromBiomass: emissions?.biomass?.totalEnergyContentFromBiomass,
                  }
                : {
                    contains: this.form.controls.contains.value,
                  };
            return this.aerService.postTaskSave(
              buildTaskData(payload, {
                biomass: biomass,
                sourceStreams: this.form.controls.sourceStreams.value,
                reportableEmissions: calculateFallbackReportableEmissions(
                  this.form.controls.contains.value,
                  emissions?.totalFossilEmissions,
                  emissions?.biomass?.totalNonSustainableBiomassEmissions,
                ),
              }),
              undefined,
              false,
              'FALLBACK',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
