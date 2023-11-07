import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { map } from 'rxjs';

import { StoreContextResolver } from '@shared/store-resolver/store-context.resolver';

import { BackLinkService } from '../../../back-link/back-link.service';
import { resolveRequestType } from '../../../store-resolver/request-type.resolver';

@Component({
  selector: 'app-peer-reviewer-submitted',
  templateUrl: './peer-review-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewSubmittedComponent implements OnInit {
  requestType = resolveRequestType(this.location.path());
  isAction =
    this.location.path().split('/')[2].includes('action') ||
    (this.location.path().split('/').length > 6 && this.location.path().split('/')[6].includes('action'));
  action$ = this.storeResolver.getStore(this.requestType, this.isAction).pipe(
    map((state) => ({
      decision: state.decision || state.requestActionItem?.payload?.decision,
      submitter: state.requestActionSubmitter || state.requestActionItem?.submitter,
      creationDate: state.requestActionCreationDate || state.requestActionItem?.creationDate,
    })),
  );

  constructor(
    private readonly backLinkService: BackLinkService,
    private readonly storeResolver: StoreContextResolver,
    private readonly location: Location,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
