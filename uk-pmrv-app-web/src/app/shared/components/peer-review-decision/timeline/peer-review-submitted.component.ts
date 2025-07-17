import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { resolveRequestType } from '@shared/store-resolver/request-type.resolver';
import { StoreContextResolver } from '@shared/store-resolver/store-context.resolver';

@Component({
  selector: 'app-peer-reviewer-submitted',
  templateUrl: './peer-review-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewSubmittedComponent implements OnInit {
  requestType = resolveRequestType(this.location.path());
  isAction =
    this.location.path().split('/')[2].includes('action') ||
    this.location.path().split('/')[1].includes('actions') ||
    ((this.location.path().split('/')[3] === 'workflows' || this.location.path().split('/')[4] === 'workflows') &&
      (this.location.path().split('/')[6].includes('action') ||
        this.location.path().split('/')[5].includes('actions') ||
        this.location.path().split('/')[6].includes('actions')));
  action$ = this.storeResolver.getStore(this.requestType, this.isAction).pipe(
    map((state) => ({
      decision: state.decision || state.requestActionItem?.payload?.decision || state.action?.payload?.decision,
      submitter: state.requestActionSubmitter || state.requestActionItem?.submitter || state.action?.submitter,
      creationDate:
        state.requestActionCreationDate || state.requestActionItem?.creationDate || state.action?.creationDate,
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
