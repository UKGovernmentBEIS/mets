import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BackLinkService } from '../../shared/back-link/back-link.service';
import { PermitSurrenderStore } from '../store/permit-surrender.store';

@Component({
  selector: 'app-submitted',
  template: `
    <app-request-action-heading
      headerText="Surrender your permit"
      [timelineCreationDate]="store.select('requestActionCreationDate') | async"
    >
    </app-request-action-heading>
    <app-permit-surrender-summary></app-permit-surrender-summary>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent implements OnInit {
  constructor(private readonly backLinkService: BackLinkService, readonly store: PermitSurrenderStore) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
