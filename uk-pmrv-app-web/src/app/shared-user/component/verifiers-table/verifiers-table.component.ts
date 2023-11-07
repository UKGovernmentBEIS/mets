import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { map, Observable, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain, selectUserId } from '@core/store/auth';

import { GovukSelectOption, GovukTableColumn, GovukValidators } from 'govuk-components';

import { UserAuthorityInfoDTO, UsersAuthoritiesInfoDTO } from 'pmrv-api';

type TableData = UserAuthorityInfoDTO & { name: string; deleteBtn: null };

const activeVerifierAdminValidator = GovukValidators.builder(
  'You must have an active verifier admin on your account',
  (verifiers: UntypedFormArray) =>
    (verifiers.value as UserAuthorityInfoDTO[])?.find(
      (item) => item.roleCode === 'verifier_admin' && item.authorityStatus === 'ACTIVE',
    )
      ? null
      : { noActiveVerifier: true },
);

@Component({
  selector: 'app-verifiers-table',
  templateUrl: './verifiers-table.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class VerifiersTableComponent implements OnInit {
  @Input() verifiersAuthorities: Observable<UsersAuthoritiesInfoDTO>;
  @Output() readonly verifiersFormSubmitted = new EventEmitter<UntypedFormGroup>();
  @Output() readonly discard = new EventEmitter<void>();

  verifiers$: Observable<UserAuthorityInfoDTO[]>;
  isEditable$: Observable<boolean>;
  roleCodes: GovukSelectOption<string>[] = [
    { text: 'Verifier admin', value: 'verifier_admin' },
    { text: 'Verifier', value: 'verifier' },
  ];
  authorityStatuses: GovukSelectOption<string>[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];
  authorityStatusesAccepted: GovukSelectOption<string>[] = [
    { text: 'Accepted', value: 'ACCEPTED' },
    { text: 'Active', value: 'ACTIVE' },
  ];
  editableCols: GovukTableColumn<TableData>[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'roleName', header: 'User Type' },
    { field: 'authorityStatus', header: 'Account status' },
    { field: 'deleteBtn', header: undefined },
  ];
  nonEditableCols = this.editableCols.slice(0, 2);
  verifiersForm = this.fb.group({
    verifiersArray: this.fb.array([], { validators: activeVerifierAdminValidator }),
  });
  userId$ = this.authStore.pipe(selectUserId, takeUntil(this.destroy$));
  currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  domainUrlPrefix$ = this.currentDomain$.pipe(map((domain) => (domain === 'AVIATION' ? '/aviation' : '')));
  verifierUserLink = this.router.url.endsWith('/verifiers') ? null : 'verifiers';

  constructor(
    readonly authStore: AuthStore,
    private readonly router: Router,
    private readonly fb: UntypedFormBuilder,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.isEditable$ = this.verifiersAuthorities.pipe(map((va) => va.editable));
    this.verifiers$ = this.verifiersAuthorities.pipe(map((va) => va.authorities));
  }

  get verifiersArray() {
    return this.verifiersForm.get('verifiersArray') as UntypedFormArray;
  }

  onSubmit() {
    if (this.verifiersForm.dirty) {
      this.verifiersFormSubmitted.emit(this.verifiersForm);
    }
  }
}
