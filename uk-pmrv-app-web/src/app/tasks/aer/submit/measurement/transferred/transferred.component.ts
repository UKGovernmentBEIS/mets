import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { buildTaskData } from '../measurement';
import { AER_MEASUREMENT_FORM, getCompletionStatus } from '../measurement-status';
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

  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_MEASUREMENT_FORM) readonly form: UntypedFormGroup,
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
              getCompletionStatus(this.taskKey, payload, index, false),
              this.taskKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const measurement = monitoringApproachEmissions[this.taskKey] as any;

    const emissionPointEmission = measurement?.emissionPointEmissions?.[index];

    const entryAccountingForTransfer = this.form.get('entryAccountingForTransfer').value;

    const transfer = {
      transfer: {
        entryAccountingForTransfer: entryAccountingForTransfer,
        ...(entryAccountingForTransfer
          ? {
              transferDirection: this.form.get('transferDirection').value,
              installationDetailsType: emissionPointEmission?.transfer?.installationDetailsType ?? null,
              installationEmitter: emissionPointEmission?.transfer?.installationEmitter ?? null,
              installationDetails: emissionPointEmission?.transfer?.installationDetails ?? null,
            }
          : {}),
        transferType: this.taskKey === 'MEASUREMENT_CO2' ? 'TRANSFER_CO2' : 'TRANSFER_N2O',
      },
    };

    const emissionPointEmissions = measurement.emissionPointEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...transfer,
          }
        : item,
    );

    const data = buildTaskData(this.taskKey, payload, emissionPointEmissions);

    return data;
  }

  private navigateNext() {
    let nextStep = '';

    const entryAccountingForTransfer = this.form.get('entryAccountingForTransfer').value;

    if (entryAccountingForTransfer) {
      nextStep = '../transferred-details';
    } else {
      nextStep = '../date-range';
    }

    this.router.navigate([nextStep], { relativeTo: this.route });
  }
}
