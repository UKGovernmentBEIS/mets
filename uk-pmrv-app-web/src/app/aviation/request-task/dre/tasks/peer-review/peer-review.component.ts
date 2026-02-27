import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-dre-peer-review',
  imports: [GovukComponentsModule, SharedModule],
  template: `
    <app-peer-review-shared></app-peer-review-shared>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DrePeerReviewComponent {}
