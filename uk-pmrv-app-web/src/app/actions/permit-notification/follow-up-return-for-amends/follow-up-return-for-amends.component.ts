import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitNotificationService } from '../core/permit-notification.service';

@Component({
  selector: 'app-follow-up-return-for-amends',
  templateUrl: './follow-up-return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpReturnForAmendsComponent {
  constructor(
    public permitNotificationService: PermitNotificationService,
    public route: ActivatedRoute,
  ) {}

  data$: Observable<any> = this.permitNotificationService.getPayload().pipe(map((results) => results?.decisionDetails));
}
