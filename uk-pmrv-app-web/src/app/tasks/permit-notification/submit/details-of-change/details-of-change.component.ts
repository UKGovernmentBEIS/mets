import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitNotification } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationService } from '../../core/permit-notification.service';
import { PERMIT_NOTIFICATION_TASK_FORM } from '../../core/permit-notification-task-form.token';
import { detailsOfChangeFromProvider } from './details-of-change-from.provider';

@Component({
  selector: 'app-details-of-change',
  templateUrl: './details-of-change.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [detailsOfChangeFromProvider],
})
export class DetailsOfChangeComponent implements PendingRequest {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  notificationTypeOptions: PermitNotification['type'][] = [
    'TEMPORARY_FACTOR',
    'TEMPORARY_CHANGE',
    'CESSATION',
    'NON_SIGNIFICANT_CHANGE',
  ];

  constructor(
    @Inject(PERMIT_NOTIFICATION_TASK_FORM) readonly form: UntypedFormGroup,
    readonly permitNotificationService: PermitNotificationService,
    readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../description'], { relativeTo: this.route });
    } else {
      this.permitNotificationService
        .postTaskSave(
          {
            permitNotification: {
              type: this.form.get('type').value,
            },
          },
          {},
          false,
          'DETAILS_CHANGE',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../description'], { relativeTo: this.route }));
    }
  }
}
