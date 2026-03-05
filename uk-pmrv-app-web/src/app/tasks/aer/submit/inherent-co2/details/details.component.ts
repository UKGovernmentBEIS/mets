import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { buildTaskData } from '@tasks/aer/submit/inherent-co2/inherent-co2';

import {
  AerApplicationSubmitRequestTaskPayload,
  AerInherentReceivingTransferringInstallation,
  AerMonitoringApproachEmissions,
  InstallationDetails,
  InstallationEmitter,
} from 'pmrv-api';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  isEditable$ = this.aerService.isEditable$;
  inherentInstallation$ = combineLatest([this.aerService.aerInherentInstallations$, this.index$]).pipe(
    map(([installations, index]) => installations?.[index]?.inherentReceivingTransferringInstallation),
  );
  heading$ = this.inherentInstallation$.pipe(
    map((inherentInstallation) =>
      inherentInstallation?.inherentCO2Direction === 'RECEIVED_FROM_ANOTHER_INSTALLATION'
        ? 'Provide details of the installation you received the emissions from'
        : 'Provide details of the installation you exported the emissions to',
    ),
  );

  transfer$ = this.inherentInstallation$.pipe(
    map((inherentInstallation) => {
      return {
        installationDetailsType: inherentInstallation?.installationDetailsType,
        ...inherentInstallation?.inherentReceivingTransferringInstallationDetailsType,
      };
    }),
  );

  constructor(
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(form: UntypedFormGroup): void {
    const nextRoute = '../instruments';
    if (!form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      combineLatest([
        this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>,
        this.aerService.aerInherentInstallations$,
        this.index$,
      ])
        .pipe(
          first(),
          switchMap(([payload, installations, index]) =>
            this.aerService.postTaskSave(
              this.getFormData(form, payload, installations, index),
              undefined,
              false,
              'INHERENT_CO2',
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
    installations: AerInherentReceivingTransferringInstallation[],
    index: number,
  ): { monitoringApproachEmissions: { [key: string]: AerMonitoringApproachEmissions } } {
    const detailsType =
      form.controls.installationDetailsType.value === 'INSTALLATION_EMITTER'
        ? {
            installationEmitter: {
              emitterId: form.controls.emitterId.value,
              email: form.controls.email.value,
            } as InstallationEmitter,
          }
        : {
            installationDetails: {
              city: form.controls.city.value,
              email: form.controls.email2.value,
              installationName: form.controls.installationName.value,
              line1: form.controls.line1.value,
              line2: form.controls?.line2.value,
              postcode: form.controls.postcode.value,
            } as InstallationDetails,
          };

    const newInstallation = {
      ...installations?.[index]?.inherentReceivingTransferringInstallation,
      installationDetailsType: form.controls.installationDetailsType.value,
      inherentReceivingTransferringInstallationDetailsType: detailsType,
    };

    return buildTaskData(payload, newInstallation, index);
  }
}
