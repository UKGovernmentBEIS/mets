import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { Observable, shareReplay } from 'rxjs';
import { map } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserRoleType } from '@core/store/auth';
import { AnnualEmissionTarget } from '@permit-application/review/determination/determination.type';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { FileInfoDTO } from 'pmrv-api';

import { PermitVariationStore } from '../../store/permit-variation.store';
import { getDecisionNameMap, getDeterminationTypeMap } from '../decisionMap';
import { reasonTemplatesMap } from '../determination/reason-template/reason-template.type';
import { getVariationScheduleItems } from '../review';

@Component({
  selector: 'app-permit-variation-decision-summary',
  templateUrl: './decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DecisionSummaryComponent implements OnInit {
  urlRequestType = this.store.urlRequestType;
  isVariationRegulatorLed = this.store.isVariationRegulatorLedRequest;

  header$ = this.store.pipe(
    map((state) =>
      this.store.isVariationRegulatorLedRequest
        ? getDecisionNameMap()['GRANTED']
        : getDecisionNameMap()[state.determination.type],
    ),
  );

  decision$ = this.store.pipe(
    map((state) =>
      this.store.isVariationRegulatorLedRequest
        ? getDeterminationTypeMap()['GRANTED']
        : getDeterminationTypeMap()[state.determination.type],
    ),
  );

  isDeterminationGranted$ = this.store.pipe(
    map((state) => this.store.isVariationRegulatorLedRequest || state.determination.type === 'GRANTED'),
  );

  officialNotice$: Observable<FileInfoDTO> = this.store.pipe(
    map((state) => state?.['officialNotice']),
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
            }) as AnnualEmissionTarget,
        ),
    ),
  );

  variationScheduleItems$: Observable<string[]> = this.store.pipe(
    map((state) => getVariationScheduleItems(state)),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  reasonTemplate$ = this.store.pipe(
    map((state) =>
      state.determination.reasonTemplate === 'OTHER'
        ? state.determination.reasonTemplateOtherSummary
        : reasonTemplatesMap[state.determination.reasonTemplate],
    ),
  );

  constructor(
    readonly store: PermitVariationStore,
    private readonly authStore: AuthStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
