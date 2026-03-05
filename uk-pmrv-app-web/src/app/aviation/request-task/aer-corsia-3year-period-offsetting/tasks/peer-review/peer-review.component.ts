import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-3year-offsetting-requirements-peer-review',
  standalone: true,
  imports: [SharedModule],
  template: `
    <app-peer-review-shared></app-peer-review-shared>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerCorsia3YearOffsettingPeriodOffsettingPeerReviewComponent {}
