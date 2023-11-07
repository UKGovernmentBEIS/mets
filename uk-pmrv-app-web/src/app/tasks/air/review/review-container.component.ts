import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { reviewWizardComplete } from '@tasks/air/review/review.wizard';
import { AirService } from '@tasks/air/shared/services/air.service';

import { AirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-review-container',
  templateUrl: './review-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewContainerComponent {
  title$ = this.airService.title$;
  payload$ = this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  allowNotifyOperator$ = this.airService.requestTaskItem$.pipe(
    first(),
    map(
      (data) =>
        reviewWizardComplete(data.requestTask.payload as AirApplicationReviewRequestTaskPayload) &&
        data.allowedRequestTaskActions.includes('AIR_NOTIFY_OPERATOR_FOR_DECISION'),
    ),
  );

  constructor(
    private readonly airService: AirService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route }).then();
  }
}
