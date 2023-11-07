import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EMP_SUBMIT_TASK_TYPES, VARIATION_OPERATOR_LED_SUBMIT_TASK_TYPES } from '../submission.util';

@Component({
  selector: 'app-submission-page',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  templateUrl: './submission-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmissionPageComponent {
  private store = inject(RequestTaskStore);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  taskType = this.store.pipe(requestTaskQuery.selectRequestTaskType);
  EMP_SUBMIT_TASK_TYPES = EMP_SUBMIT_TASK_TYPES;
  VARIATION_OPERATOR_LED_SUBMIT_TASK_TYPES = VARIATION_OPERATOR_LED_SUBMIT_TASK_TYPES;

  onSubmit() {
    this.store.empDelegate
      .submitEmp()
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => this.router.navigate(['success'], { relativeTo: this.route }));
  }
}
