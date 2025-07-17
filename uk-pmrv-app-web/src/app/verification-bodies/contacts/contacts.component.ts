import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, first, merge, Observable, shareReplay, Subject, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { UsersAuthoritiesInfoDTO, VerifierAuthoritiesService, VerifierAuthorityUpdateDTO } from 'pmrv-api';

import { savePartiallyNotFoundVerifierError } from '../../verifiers/errors/business-error';

@Component({
  selector: 'app-contacts',
  template: `
    <govuk-error-summary *ngIf="isSummaryDisplayed$ | async" [form]="verifiersForm"></govuk-error-summary>
    <a routerLink="add-contact" govukButton>Add new verifier admin</a>
    <app-verifiers-table
      [verifiersAuthorities]="verifiersAuthorities$"
      (verifiersFormSubmitted)="onVerifiersFormSubmitted($event)"
      (discard)="this.refresh$.next()"></app-verifiers-table>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactsComponent implements OnInit {
  @Input() verificationBodyId: Observable<number>;

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  refresh$ = new Subject<void>();
  verifiersAuthorities$: Observable<UsersAuthoritiesInfoDTO>;
  verifiersForm: UntypedFormGroup;

  constructor(
    private readonly businessErrorService: BusinessErrorService,
    private readonly verifierAuthoritiesService: VerifierAuthoritiesService,
  ) {}

  ngOnInit(): void {
    const verifiersAuthorities$ = this.verificationBodyId.pipe(
      first(),
      switchMap((vbId) => this.verifierAuthoritiesService.getVerifierAuthoritiesByVerificationBodyId(vbId)),
    );
    this.verifiersAuthorities$ = merge(
      verifiersAuthorities$,
      this.refresh$.pipe(
        switchMap(() => this.verificationBodyId),
        switchMap((vbId) => this.verifierAuthoritiesService.getVerifierAuthoritiesByVerificationBodyId(vbId)),
      ),
    ).pipe(shareReplay({ bufferSize: 1, refCount: true }));
  }

  onVerifiersFormSubmitted(verifiersForm: UntypedFormGroup) {
    if (!verifiersForm.valid) {
      this.isSummaryDisplayed$.next(true);
      this.verifiersForm = verifiersForm;
    } else {
      this.isSummaryDisplayed$.next(false);
      const updatedVerifiersAuthorities: VerifierAuthorityUpdateDTO[] = (
        verifiersForm.get('verifiersArray') as UntypedFormArray
      ).controls
        .filter((control) => control.dirty)
        .map((control) => ({
          authorityStatus: control.value.authorityStatus,
          roleCode: control.value.roleCode,
          userId: control.value.userId,
        }));

      this.verificationBodyId
        .pipe(
          first(),
          switchMap((vbId) =>
            this.verifierAuthoritiesService.updateVerifierAuthoritiesByVerificationBodyId(
              vbId,
              updatedVerifiersAuthorities,
            ),
          ),
          catchBadRequest(ErrorCodes.AUTHORITY1006, () =>
            this.businessErrorService.showError(savePartiallyNotFoundVerifierError),
          ),
          tap(() => verifiersForm.markAsPristine()),
        )
        .subscribe(() => this.refresh$.next());
    }
  }
}
