import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationService } from '../../core/permit-notification.service';

@Component({
  selector: 'app-summary',
  standalone: false,
  templateUrl: './summary.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
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
