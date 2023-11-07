import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { VirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { VirService } from '../../../core/vir.service';

@Component({
  selector: 'app-summary',
  template: `
    <app-action-task [header]="verificationDataItem.reference | verificationReferenceTitle" [breadcrumb]="true">
      <app-verification-recommendation-item [verificationDataItem]="verificationDataItem">
      </app-verification-recommendation-item>
      <app-operator-response-item
        [reference]="reference"
        [operatorImprovementResponse]="operatorImprovementResponse$ | async"
        [attachedFiles]="documentFiles$ | async"
        [isEditable]="false"
        [isReview]="true"
      >
      </app-operator-response-item>
      <app-regulator-response-item
        [reference]="reference"
        [regulatorImprovementResponse]="regulatorImprovementResponse$ | async"
        [isEditable]="false"
        [isReview]="true"
      >
      </app-regulator-response-item>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;
  reference = this.verificationDataItem.reference;
  heading = `Respond to ${this.reference}`;
  virPayload$ = this.virService.payload$ as Observable<VirApplicationReviewedRequestActionPayload>;
  operatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.operatorImprovementResponses?.[this.reference]),
  );
  regulatorImprovementResponse$ = this.virPayload$.pipe(
    map((payload) => payload?.regulatorReviewResponse?.regulatorImprovementResponses?.[this.reference]),
  );
  documentFiles$ = this.virPayload$.pipe(
    map((payload) =>
      payload?.operatorImprovementResponses?.[this.reference]?.files
        ? this.virService.getDownloadUrlFiles(payload?.operatorImprovementResponses?.[this.reference]?.files)
        : [],
    ),
  );

  constructor(private readonly virService: VirService, private readonly route: ActivatedRoute) {}
}
