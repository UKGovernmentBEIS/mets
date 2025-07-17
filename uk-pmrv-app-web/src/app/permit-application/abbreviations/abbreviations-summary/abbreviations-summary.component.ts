import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-abbreviations-summary',
  templateUrl: './abbreviations-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AbbreviationsSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  hasAbbreviations$: Observable<boolean> = this.store.getTask('abbreviations').pipe(map((item) => !!item?.exist));

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}
}
