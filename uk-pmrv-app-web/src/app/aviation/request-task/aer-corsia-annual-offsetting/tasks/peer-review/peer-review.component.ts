import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-aer-corsia-annual-offsetting-peer-review',
  standalone: true,
  imports: [SharedModule],
  template: `
    <app-peer-review-shared></app-peer-review-shared>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerCorsiaAnnualOffsettingPeerReviewComponent {}
