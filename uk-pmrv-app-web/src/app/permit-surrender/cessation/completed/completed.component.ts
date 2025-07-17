import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { RequestActionUserInfo } from 'pmrv-api';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-completed',
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletedComponent implements OnInit {
  signatory$: Observable<string>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  operators$: Observable<string[]>;

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();

    this.signatory$ = this.store.pipe(map((state) => state?.cessationDecisionNotification?.signatory));
    this.usersInfo$ = this.store.pipe(map((state) => state?.cessationDecisionNotificationUsersInfo)) as Observable<{
      [key: string]: RequestActionUserInfo;
    }>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
  }
}
