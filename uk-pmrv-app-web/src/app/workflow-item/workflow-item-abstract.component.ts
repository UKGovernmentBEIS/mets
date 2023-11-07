import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, takeUntil } from 'rxjs';

import { DestroySubject } from '../core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '../core/store';
import { BackLinkService } from '../shared/back-link/back-link.service';

export abstract class WorkflowItemAbstractComponent {
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  accountId$ = this.route.paramMap.pipe(
    map((paramMap) => (paramMap.get('accountId') ? Number(paramMap.get('accountId')) : null)),
  );
  requestTypeUrlPath$ = this.route.data.pipe(map((data) => data.requestTypeUrlPath as string));
  requestId$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('request-id')));
  prefixUrl$ = combineLatest([this.currentDomain$, this.accountId$, this.requestTypeUrlPath$]).pipe(
    map(([currentDomain, accountId, requestTypeUrlPath]) => {
      const domainUrl = currentDomain === 'AVIATION' ? currentDomain.toLowerCase() + '/' : '';
      return accountId ? `${domainUrl}accounts/${accountId}` : `${domainUrl}workflows/${requestTypeUrlPath}`;
    }),
  );

  constructor(
    protected readonly authStore: AuthStore,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    protected readonly backLinkService: BackLinkService,
    protected readonly destroy$: DestroySubject,
  ) {}
}
