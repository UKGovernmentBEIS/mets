import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

@Component({
  selector: 'app-skip-review-confirmation-aviation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Emissions report completed">
          <p>
            <strong>Your reference code is:</strong><br />
            {{ (requestId$ | async).id }}
          </p>
        </govuk-panel>
      </div>
    </div>
    <a govukLink routerLink="/aviation/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SkipReviewConfirmationComponent extends BaseSuccessComponent {
  private store = inject(RequestTaskStore);
  protected requestId$ = this.store.pipe(requestTaskQuery.selectRequestInfo);
}
