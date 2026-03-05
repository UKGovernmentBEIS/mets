import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Subscription } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { FlightProceduresFormComponent } from '../flight-procedures-form';
import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';
import { onSubmitFlightProcedures } from '../shared/flight-procedures.functions';

@Component({
  selector: 'app-flight-procedures-list-flights',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, FlightProceduresFormComponent, ReturnToLinkComponent],
  templateUrl: './flight-procedures-list-flights.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FlightProceduresListFlightsComponent implements OnDestroy {
  formProvider = inject<FlightProceduresFormProvider>(TASK_FORM_PROVIDER);
  form = this.formProvider.flightListCompletenessDetails;
  nextStep = 'flights-covered-by-the-UK-ETS';
  pageFieldForm = 'flightListCompletenessDetails';

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
