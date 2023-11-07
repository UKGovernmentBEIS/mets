import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationService } from '../../core/permit-notification.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SummaryComponent {
  constructor(
    private readonly router: Router,
    readonly store: CommonTasksStore,
    readonly permitNotificationService: PermitNotificationService,
  ) {}
  notificationBanner = this.router.getCurrentNavigation()?.extras.state?.notification;

  notification$ = this.permitNotificationService.permitNotification$;
}
