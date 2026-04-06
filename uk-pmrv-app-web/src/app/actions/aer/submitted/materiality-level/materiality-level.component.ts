import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-materiality-level',
  standalone: false,
  template: `
    <app-action-task
      [header]="
        (isMaterialityUpdated$ | async)
          ? 'Further information of relevance to the opinion'
          : 'Materiality level and reference documents'
      "
      [breadcrumb]="true">
      <app-materiality-level-group
        [materialityLevelInfo]="materialityLevelInfo$ | async"
        [isMaterialityUpdated]="isMaterialityUpdated$ | async"></app-materiality-level-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialityLevelComponent {
  materialityLevelInfo$ = (
    this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>
  ).pipe(map((payload) => payload.verificationReport.materialityLevel));

  isMaterialityUpdated$ = this.aerService.isMaterialityUpdated$ as Observable<boolean>;

  constructor(private readonly aerService: AerService) {}
}
