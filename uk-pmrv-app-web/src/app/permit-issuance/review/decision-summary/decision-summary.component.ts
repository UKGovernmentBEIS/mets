import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { map, Observable } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store/auth';
import { AnnualEmissionTarget } from '@permit-application/review/determination/determination.type';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { FileInfoDTO } from 'pmrv-api';

import { PermitIssuanceStore } from '../../store/permit-issuance.store';
import { getDecisionNameMap, getDeterminationTypeMap } from '../decisionMap';

@Component({
  selector: 'app-permit-issuance-decision-summary',
  templateUrl: './decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DecisionSummaryComponent implements OnInit {
  urlRequestType = this.store.urlRequestType;

  header$ = this.store.pipe(map((state) => getDecisionNameMap()[state.determination.type]));

  decision$ = this.store.pipe(map((state) => getDeterminationTypeMap()[state.determination.type]));

  isDeterminationGranted$ = this.store.pipe(map((state) => state.determination.type === 'GRANTED'));

  officialNotice$: Observable<FileInfoDTO> = this.store.pipe(
    map((state) => state['officialNotice']),
  ) as Observable<FileInfoDTO>;
  permitDocument$: Observable<FileInfoDTO> = this.store.pipe(
    map((state) => (state as any)?.permitDocument),
  ) as Observable<FileInfoDTO>;
  actionId$ = this.store.pipe(map((state) => state.actionId));

  permitLink$ = this.authStore.pipe(
    selectUserRoleType,
    map((roleType) => (roleType === 'OPERATOR' ? '../..' : '..')),
  );

  signatory$ = this.store.pipe(map((state) => state.decisionNotification.signatory));
  operators$ = this.store.pipe(
    map((state) => Object.keys(state.usersInfo).filter((userId) => userId !== state.decisionNotification.signatory)),
  );

  emissionsTargets$: Observable<AnnualEmissionTarget[]> = this.store.pipe(
    map(
      (state) =>
        state.determination.annualEmissionsTargets &&
        Object.keys(state.determination?.annualEmissionsTargets).map(
          (key) =>
            ({
              year: key,
              target: state.determination.annualEmissionsTargets[key],
            } as AnnualEmissionTarget),
        ),
    ),
  );

  constructor(
    readonly store: PermitIssuanceStore,
    private readonly authStore: AuthStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
