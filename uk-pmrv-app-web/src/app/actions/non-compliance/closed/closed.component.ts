import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { NonComplianceApplicationClosedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';
import { NonComplianceService } from '../core/non-compliance.service';

@Component({
  selector: 'app-closed',
  templateUrl: './closed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ClosedComponent {
  nonCompliance$ = (
    this.nonComplianceService.getPayload() as Observable<NonComplianceApplicationClosedRequestActionPayload>
  ).pipe(map((payload) => payload));

  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  constructor(
    readonly nonComplianceService: NonComplianceService,
    readonly store: CommonActionsStore,
    private readonly router: Router,
  ) {}

  get supportingDocumentFiles$() {
    return this.nonCompliance$.pipe(
      map((nonCompliance) => this.nonComplianceService.getDownloadUrlFiles(nonCompliance?.files)),
    );
  }
}
