import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, Observable, switchMap } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { CaExternalContactDTO, CaExternalContactsService } from 'pmrv-api';

import { saveNotFoundExternalContactError } from '../../errors/business-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  contact$: Observable<CaExternalContactDTO> = this.route.data.pipe(map((x) => x?.contact));

  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);
  currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  domainUrlPrefix$ = this.currentDomain$.pipe(map((domain) => (domain === 'AVIATION' ? '/aviation' : '')));

  constructor(
    private readonly externalContactsService: CaExternalContactsService,
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  deleteExternalContact(): void {
    this.contact$
      .pipe(
        first(),
        switchMap((contact) => this.externalContactsService.deleteCaExternalContactById(contact.id)),
        catchBadRequest(ErrorCodes.EXTCONTACT1000, () =>
          this.businessErrorService.showError(saveNotFoundExternalContactError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}
