import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  distinctUntilChanged,
  map,
  merge,
  Observable,
  shareReplay,
  Subject,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AccountType } from '@core/store/auth';
import { selectCurrentDomain } from '@core/store/auth/auth.selectors';
import { AuthStore } from '@core/store/auth/auth.store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { GovukSelectOption } from 'govuk-components';

import {
  AccountContactDTO,
  AccountContactVbInfoResponse,
  UserAuthorityInfoDTO,
  UsersAuthoritiesInfoDTO,
  VBSiteContactsService,
  VerifierAuthoritiesService,
  VerifierAuthorityUpdateDTO,
} from 'pmrv-api';

import { savePartiallyNotFoundSiteContactsError, savePartiallyNotFoundVerifierError } from './errors/business-error';

@Component({
  selector: 'app-verifiers',
  templateUrl: './verifiers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifiersComponent implements OnInit {
  siteContact$: Observable<AccountContactVbInfoResponse>;
  siteContactsPage$ = new BehaviorSubject<number>(null);
  siteContactsPageSize = 50;

  refresh$ = new Subject<void>();
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  addNewUserForm = this.fb.group({
    roleCode: ['verifier'],
  });
  verifiersAuthorities$: Observable<UsersAuthoritiesInfoDTO>;
  verifiers$: Observable<UserAuthorityInfoDTO[]>;
  isEditable$: Observable<boolean>;
  verifiersForm: UntypedFormGroup;
  domain: AccountType;

  roleCodes: GovukSelectOption<string>[] = [
    { text: 'Verifier admin', value: 'verifier_admin' },
    { text: 'Verifier', value: 'verifier' },
  ];

  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  constructor(
    readonly authService: AuthService,
    private readonly fb: UntypedFormBuilder,
    private readonly verifierAuthoritiesService: VerifierAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly vbSiteContactsService: VBSiteContactsService,
    private readonly businessErrorService: BusinessErrorService,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.verifiersAuthorities$ = merge(
      (this.route.data as Observable<{ verifiers: UsersAuthoritiesInfoDTO }>).pipe(map((state) => state.verifiers)),
      this.refresh$.pipe(switchMap(() => this.verifierAuthoritiesService.getVerifierAuthorities())),
    ).pipe(shareReplay({ bufferSize: 1, refCount: true }));

    this.verifiers$ = this.verifiersAuthorities$.pipe(map((state) => state.authorities));
    this.isEditable$ = this.verifiersAuthorities$.pipe(map((state) => state.editable));

    this.siteContact$ = combineLatest([
      this.currentDomain$,
      merge(
        this.refresh$.pipe(switchMap(() => this.siteContactsPage$)),
        this.siteContactsPage$.pipe(distinctUntilChanged()),
      ),
    ]).pipe(
      switchMap(([currentDomain, page]) =>
        this.vbSiteContactsService.getVbSiteContacts(currentDomain, page - 1, this.siteContactsPageSize),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );
  }

  addNewUser(): void {
    this.router.navigate(['add'], { relativeTo: this.route, queryParams: this.addNewUserForm.value });
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

      this.verifierAuthoritiesService
        .updateVerifierAuthorities(updatedVerifiersAuthorities)
        .pipe(
          catchBadRequest(ErrorCodes.AUTHORITY1006, () =>
            this.businessErrorService.showError(savePartiallyNotFoundVerifierError),
          ),
          tap(() => verifiersForm.markAsPristine()),
        )
        .subscribe(() => this.refresh$.next());
    }
  }

  saveSiteContacts(contacts: AccountContactDTO[]): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      this.domain = domain;
    });

    this.vbSiteContactsService
      .updateVbSiteContacts(this.domain, contacts)
      .pipe(
        catchBadRequest([ErrorCodes.ACCOUNT1005, ErrorCodes.AUTHORITY1006], () =>
          this.businessErrorService.showError(savePartiallyNotFoundSiteContactsError),
        ),
      )
      .subscribe(() => this.refresh$.next());
  }
}
