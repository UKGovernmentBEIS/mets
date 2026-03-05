import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { installationDescriptionFormProvider } from './installation-description-form.provider';

@Component({
  selector: 'app-installation-description',
  templateUrl: './installation-description.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [installationDescriptionFormProvider],
})
export class InstallationDescriptionComponent extends SectionComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    this.store
      .postTask('installationDescription', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.navigateSubmitSection('summary', 'details'));
  }
}
