import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { debounceTime, distinctUntilChanged, iif, map, mergeMap, Observable, of, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { OpinionStatementEmissionsCalculationService } from '@shared/services/opinion-statement-emissions-calculation.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { reviewEmissionsFormProvider } from '@tasks/aer/verification-submit/opinion-statement/review-emissions/review-emissions.form.provider';
import { isEqual } from 'lodash-es';

import { AerApplicationSubmitRequestTaskPayload, OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-review-emissions',
  templateUrl: './review-emissions.component.html',
  providers: [reviewEmissionsFormProvider, OpinionStatementEmissionsCalculationService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewEmissionsComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;
  aerPayload$ = this.aerService
    .getPayload()
    .pipe(map((payload) => (payload as AerApplicationSubmitRequestTaskPayload).aer));
  emissionsLabel = 'Reportable emissions (include non sustainable biomass)';
  biomassLabel = 'Sustainable biomass';
  pfcEmissionsLabel = 'Reportable emissions';
  suffixLabel = 'tonnes CO2e';
  totalEmissions$: Observable<string> = this.form.valueChanges.pipe(
    startWith(this.form.value),
    debounceTime(200),
    distinctUntilChanged(isEqual),
    mergeMap((data: Pick<OpinionStatement, 'operatorEmissionsAcceptable' | 'monitoringApproachTypeEmissions'>) =>
      iif(
        () => data.operatorEmissionsAcceptable === false,
        of(
          this.opinionStatementEmissionsCalculationService.calculateTotalEmissions(
            data?.monitoringApproachTypeEmissions,
          ),
        ),
        of('0'),
      ),
    ),
  );

  totalBiomass$: Observable<string> = this.form.valueChanges.pipe(
    startWith(this.form.value),
    debounceTime(200),
    distinctUntilChanged(isEqual),
    mergeMap((data: Pick<OpinionStatement, 'operatorEmissionsAcceptable' | 'monitoringApproachTypeEmissions'>) =>
      iif(
        () => data.operatorEmissionsAcceptable === false,
        of(
          this.opinionStatementEmissionsCalculationService.calculateTotalBiomass(data?.monitoringApproachTypeEmissions),
        ),
        of('0'),
      ),
    ),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly opinionStatementEmissionsCalculationService: OpinionStatementEmissionsCalculationService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../additional-changes'], { relativeTo: this.route }).then();
    } else {
      this.aerService
        .getMappedPayload<OpinionStatement>(['verificationReport', 'opinionStatement'])
        .pipe(
          switchMap((opinionStatement) => {
            const opinionStatementData = this.form.value as Pick<
              OpinionStatement,
              'operatorEmissionsAcceptable' | 'monitoringApproachTypeEmissions'
            >;
            if (opinionStatementData.operatorEmissionsAcceptable === true) {
              opinionStatementData.monitoringApproachTypeEmissions = null;
            }
            return this.aerService.postVerificationTaskSave(
              {
                opinionStatement: {
                  ...opinionStatement,
                  ...opinionStatementData,
                },
              },
              false,
              'opinionStatement',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../additional-changes'], { relativeTo: this.route }));
    }
  }
}
