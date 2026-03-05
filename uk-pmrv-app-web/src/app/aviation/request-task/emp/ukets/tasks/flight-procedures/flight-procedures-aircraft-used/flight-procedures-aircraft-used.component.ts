import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Subscription } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { FlightProceduresFormComponent } from '../flight-procedures-form';
import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';
import { onSubmitFlightProcedures } from '../shared/flight-procedures.functions';

@Component({
  selector: 'app-flight-procedures-aircraft-used',
  standalone: true,
  imports: [SharedModule, FlightProceduresFormComponent, ReturnToLinkComponent],
  templateUrl: './flight-procedures-aircraft-used.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FlightProceduresAircraftUsedComponent implements OnDestroy {
  formProvider = inject<FlightProceduresFormProvider>(TASK_FORM_PROVIDER);
  form = this.formProvider.aircraftUsedDetailsCtrl;
  nextStep = 'completeness-of-the-flights-list';
  pageFieldForm = 'aircraftUsedDetails';

  private store = inject(RequestTaskStore);
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private subscription: Subscription;

  onSubmit() {
    if (this.form?.valid) {
      this.subscription = onSubmitFlightProcedures(
        this.store,
        this.form,
        this.pageFieldForm,
        this.nextStep,
        this.router,
        this.route,
        this.pendingRequestService,
      );
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}
