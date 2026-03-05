import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitSurrenderReviewDeterminationReject } from 'pmrv-api';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

@Component({
  selector: 'app-reject-determination-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() rejectDetermination: PermitSurrenderReviewDeterminationReject;

  isEditable$ = this.store.pipe(map((state) => state?.isEditable));

  constructor(
    private readonly store: PermitSurrenderStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    const baseUrl = !wizardStep ? '../../' : '../';
    this.router.navigate([baseUrl + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
