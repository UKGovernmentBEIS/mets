import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-additional-info',
  template: `
    <app-page-heading>Additional information</app-page-heading>
    <app-additional-info-group
      [aerData]="aerData$ | async"
      [additionalDocumentFiles]="additionalDocumentFiles$ | async"></app-additional-info-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalInfoComponent {
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  additionalDocumentFiles$ = this.aerData$.pipe(
    map((data) =>
      data.additionalDocuments.exist ? this.aerService.getDownloadUrlFiles(data.additionalDocuments.documents) : [],
    ),
  );

  constructor(private readonly aerService: AerService) {}
}
