import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ApproachTaskPipe } from '@permit-application/approaches/approach-task.pipe';

import { InherentCO2MonitoringApproach, InherentReceivingTransferringInstallation } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  transfer$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe.transform('INHERENT_CO2').pipe(
        map((response: InherentCO2MonitoringApproach) => {
          const installation = response?.inherentReceivingTransferringInstallations?.[index];
          return {
            installationDetailsType: installation?.installationDetailsType,
            ...installation?.inherentReceivingTransferringInstallationDetailsType,
          };
        }),
      ),
    ),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private approachTaskPipe: ApproachTaskPipe,
  ) {}

  onContinue(form: UntypedFormGroup): void {
    combineLatest([
      this.index$,
      this.store.findTask<InherentReceivingTransferringInstallation[]>(
        'monitoringApproaches.INHERENT_CO2.inherentReceivingTransferringInstallations',
      ),
      this.route.data,
    ])
      .pipe(
        first(),
        switchMap(([index, installations, data]) => {
          const inherentReceivingTransferringInstallations = installations.map((installation, idx) =>
            index === idx
              ? {
                  ...installation,
                  installationDetailsType: form.controls.installationDetailsType.value,
                  ...(form.controls.installationDetailsType.value === 'INSTALLATION_EMITTER'
                    ? {
                        inherentReceivingTransferringInstallationDetailsType: {
                          installationEmitter: {
                            emitterId: form.controls.emitterId.value,
                            email: form.controls.email.value,
                          },
                        },
                      }
                    : {}),
                  ...(form.controls.installationDetailsType.value === 'INSTALLATION_DETAILS'
                    ? {
                        inherentReceivingTransferringInstallationDetailsType: {
                          installationDetails: {
                            city: form.controls.city.value,
                            email: form.controls.email2.value,
                            installationName: form.controls.installationName.value,
                            line1: form.controls.line1.value,
                            line2: form.controls?.line2.value,
                            postcode: form.controls.postcode.value,
                          },
                        },
                      }
                    : {}),
                }
              : installation,
          );

          return this.store
            .patchTask(data.taskKey, { inherentReceivingTransferringInstallations }, false, data.statusKey)
            .pipe(this.pendingRequest.trackRequest());
        }),
      )
      .subscribe(() => this.router.navigate(['../instruments'], { relativeTo: this.route }));
  }
}
