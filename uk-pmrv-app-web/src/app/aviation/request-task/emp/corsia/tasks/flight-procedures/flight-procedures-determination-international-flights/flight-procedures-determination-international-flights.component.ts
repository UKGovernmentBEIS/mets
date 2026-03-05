import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { FlightProceduresFormComponent } from '../flight-procedures-form';
import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';

@Component({
  selector: 'app-flight-procedures-determination-international-flights',
  templateUrl: './flight-procedures-determination-international-flights.component.html',
  standalone: true,
  imports: [SharedModule, FlightProceduresFormComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FlightProceduresDeterminationInternationalFlightsComponent {
  protected form = this.formProvider.internationalFlightsDeterminationCtrl;

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: FlightProceduresFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.form.invalid) return;

    this.store.empCorsiaDelegate
      .saveEmp({ flightAndAircraftProcedures: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empCorsiaDelegate.setFlightProcedures(this.formProvider.getFormValue());
        this.router.navigate(['..', 'determination-international-flights-offset'], { relativeTo: this.route });
      });
  }
}
