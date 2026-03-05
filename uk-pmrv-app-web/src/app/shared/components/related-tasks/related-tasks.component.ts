import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store';

import { ItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-related-tasks',
  templateUrl: './related-tasks.component.html',
  styles: `
    .govuk-body {
      float: left;
    }
    .govuk-button {
      margin-bottom: 0;
    }
    .govuk-button--secondary {
      float: right;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelatedTasksComponent {
  @Input() items: ItemDTO[];
  @Input() heading = 'Related tasks';
  @Input() noBorders = false;

  isAviation$: Observable<boolean> = this.authStore.pipe(
    selectCurrentDomain,
    map((v) => v === 'AVIATION'),
  );

  constructor(
    public readonly router: Router,
    public readonly authStore: AuthStore,
  ) {}
}
