import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfCO2Emissions } from 'pmrv-api';

import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { AER_CALCULATION_EMISSIONS_FORM, buildTaskData } from '../calculation-emissions';
import { transferredFormProvider } from './transferred-form.provider';

@Component({
  selector: 'app-transferred',
  templateUrl: './transferred.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [transferredFormProvider],
})
export class TransferredComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_CALCULATION_EMISSIONS_FORM) readonly form: UntypedFormGroup,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.navigateNext();
    } else {
      combineLatest([this.payload$, this.index$])
        .pipe(
          first(),
          switchMap(([payload, index]) =>
            this.aerService.postTaskSave(
              this.buildData(payload, index),
              undefined,
              getCompletionStatus('CALCULATION_CO2', payload, index, false),
              'CALCULATION_CO2',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const sourceStreamEmission = calculation.sourceStreamEmissions?.[index];

    const entryAccountingForTransfer = this.form.get('entryAccountingForTransfer').value;

    const transfer = {
      transfer: {
        transferType: 'TRANSFER_CO2',
        entryAccountingForTransfer: entryAccountingForTransfer,
        ...(entryAccountingForTransfer
          ? {
              transferDirection: this.form.get('transferDirection').value,
              installationDetailsType: sourceStreamEmission?.transfer?.installationDetailsType ?? null,
              installationEmitter: sourceStreamEmission?.transfer?.installationEmitter ?? null,
              installationDetails: sourceStreamEmission?.transfer?.installationDetails ?? null,
            }
          : {}),
      },
    };

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...transfer,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    let nextStep;

    const entryAccountingForTransfer = this.form.get('entryAccountingForTransfer').value;

    if (entryAccountingForTransfer) {
      nextStep = '../transferred-details';
    } else {
      nextStep = '../date-range';
    }

    this.router.navigate([nextStep], { relativeTo: this.route });
  }
}
