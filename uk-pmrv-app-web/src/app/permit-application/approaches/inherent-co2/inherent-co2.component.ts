import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { InherentCO2MonitoringApproach } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { SectionComponent } from '../../shared/section/section.component';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { ApproachTaskPipe } from '../approach-task.pipe';
import { inherentCO2FormProvider } from './inherent-co2-form.provider';

@Component({
  selector: 'app-inherent-co2',
  templateUrl: './inherent-co2.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [inherentCO2FormProvider],
})
export class InherentCO2Component extends SectionComponent implements PendingRequest {
  displayErrors$ = new BehaviorSubject<boolean>(false);

  installations$ = this.approachTaskPipe.transform('INHERENT_CO2').pipe(
    map((response: InherentCO2MonitoringApproach) => {
      return response?.inherentReceivingTransferringInstallations;
    }),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly route: ActivatedRoute,
    private approachTaskPipe: ApproachTaskPipe,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.form.markAllAsTouched();
      this.displayErrors$.next(true);
    } else {
      this.route.data
        .pipe(
          first(),
          switchMap((data) => this.store.postStatus('INHERENT_CO2', true, data.permitTask)),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateSubmitSection('summary', 'inherent-co2'));
    }
  }
}
