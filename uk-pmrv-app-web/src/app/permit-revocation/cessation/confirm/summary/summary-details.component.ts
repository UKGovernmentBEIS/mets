import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable } from 'rxjs';

import { PermitCessation } from 'pmrv-api';

import { AuthStore, selectUserRoleType, UserState } from '../../../../core/store';
import { OfficialNoticeTypeMap } from '../core/cessation';

@Component({
  selector: 'app-revocation-cessation-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent implements OnInit {
  @Input() cessation: PermitCessation;
  @Input() allowancesSurrenderRequired: boolean;
  @Input() isEditable: boolean;

  userRoleType$: Observable<UserState['roleType']>;
  readonly officialNoticeTypeMap = OfficialNoticeTypeMap;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
  ) {}

  ngOnInit(): void {
    this.userRoleType$ = this.authStore.pipe(selectUserRoleType);
  }
  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
