import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-additional-info',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Additional information">
      <app-additional-info-group
        [aerData]="aerData$ | async"
        [additionalDocumentFiles]="additionalDocumentFiles$ | async"
      ></app-additional-info-group>
      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalInfoComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  additionalDocumentFiles$ = this.aerData$.pipe(
    map((data) =>
      data.additionalDocuments.exist ? this.aerService.getDownloadUrlFiles(data.additionalDocuments.documents) : [],
    ),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
