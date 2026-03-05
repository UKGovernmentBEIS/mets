import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { environmentalSystemFormProvider } from './environmental-system-form.provider';

@Component({
  selector: 'app-environmental-system',
  templateUrl: './environmental-system.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [environmentalSystemFormProvider],
})
export class EnvironmentalSystemComponent extends SectionComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    this.store
      .postTask('environmentalManagementSystem', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.navigateSubmitSection('summary', 'management-procedures'));
  }
}
