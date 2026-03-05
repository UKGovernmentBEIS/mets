import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { buildTaskData, calculateFallbackReportableEmissions } from '@tasks/aer/submit/fallback/fallback';
import { totalEmissionsFormProvider } from '@tasks/aer/submit/fallback/total-emissions/total-emissions-form.provider';

import { AerApplicationSubmitRequestTaskPayload, AerMonitoringApproachEmissions, FallbackEmissions } from 'pmrv-api';

@Component({
  selector: 'app-total-emissions',
  templateUrl: './total-emissions.component.html',
  providers: [totalEmissionsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;
  containsBiomass$ = (this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>).pipe(
    map((payload) => (payload.aer?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions)?.biomass?.contains),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = '../upload-documents';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      combineLatest([
        this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>,
        this.containsBiomass$,
      ])
        .pipe(
          first(),
          switchMap(([payload, containsBiomass]) =>
            this.aerService.postTaskSave(
              this.getFormData(this.form, payload, containsBiomass),
              undefined,
              false,
              'FALLBACK',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getFormData(
    form: UntypedFormGroup,
    payload: AerApplicationSubmitRequestTaskPayload,
    containsBiomass: boolean,
  ): { monitoringApproachEmissions: { [key: string]: AerMonitoringApproachEmissions } } {
    return buildTaskData(payload, {
      totalFossilEmissions: this.form.controls.totalFossilEmissions.value,
      totalFossilEnergyContent: this.form.controls.totalFossilEnergyContent.value,
      reportableEmissions: calculateFallbackReportableEmissions(
        containsBiomass,
        this.form.controls.totalFossilEmissions.value,
        this.form.controls?.totalNonSustainableBiomassEmissions?.value,
      ),
      biomass: containsBiomass
        ? {
            contains: true,
            totalSustainableBiomassEmissions: this.form.controls.totalSustainableBiomassEmissions.value,
            totalEnergyContentFromBiomass: this.form.controls.totalEnergyContentFromBiomass.value,
            totalNonSustainableBiomassEmissions: this.form.controls.totalNonSustainableBiomassEmissions.value,
          }
        : {
            contains: false,
          },
    });
  }
}
