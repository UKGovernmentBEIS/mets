import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  combineLatest,
  distinctUntilChanged,
  filter,
  map,
  merge,
  Observable,
  ReplaySubject,
  shareReplay,
  Subject,
  switchMap,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';

import { GovukSelectOption, GovukTableColumn, GovukValidators } from 'govuk-components';

import {
  AccountContactInfoDTO,
  CaSiteContactsService,
  RegulatorAuthoritiesService,
  RegulatorUserAuthorityInfoDTO,
} from 'pmrv-api';

import { savePartiallyNotFoundSiteContactError } from '../errors/business-error';

type TableData = AccountContactInfoDTO & { user: RegulatorUserAuthorityInfoDTO };

@Component({
  selector: 'app-site-contacts',
  standalone: false,
  templateUrl: './site-contacts.component.html',
  providers: [UserFullNamePipe, DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SiteContactsComponent implements OnInit {
  private readonly isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  page$ = new ReplaySubject<number>(1);
  count$: Observable<number>;
  columns: GovukTableColumn<TableData>[] = [
    { field: 'accountName', header: this.isAviation ? 'Account' : 'Permit holding account', isHeader: true },
    { field: 'user', header: 'Assigned to' },
  ];
  tableData$: Observable<TableData[]>;
  isEditable$: Observable<boolean>;
  readonly pageSize = 50;
  form = this.fb.group({ siteContacts: this.fb.array([]) });
  assigneeOptions$: Observable<GovukSelectOption<string>[]>;
  refresh$ = new Subject<void>();
  searchForm = this.fb.group({
    term: [
      null,
      {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(256, 'Enter up to 256 characters'),
        ],
      },
    ],
  });
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  private readonly term$ = this.route.queryParamMap.pipe(
    map((params) => params.get('term')?.trim() || null),
    distinctUntilChanged(),
    shareReplay({ bufferSize: 1, refCount: false }),
  );

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly siteContactsService: CaSiteContactsService,
    private readonly fullNamePipe: UserFullNamePipe,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    const activatedTab$ = this.route.fragment.pipe(filter((fragment) => fragment === 'site-contacts'));

    const regulators$ = merge(activatedTab$, this.refresh$).pipe(
      switchMap(() => this.regulatorAuthoritiesService.getCaRegulators()),
      map((state) => state.caUsers),
      shareReplay({ bufferSize: 1, refCount: false }),
    );

    this.term$.pipe(takeUntil(this.destroy$)).subscribe((term) => this.termCtrl.setValue(term, { emitEvent: false }));

    const contacts$ = combineLatest([
      merge(this.refresh$.pipe(switchMap(() => this.page$)), this.page$.pipe(distinctUntilChanged())),
      this.term$,
      activatedTab$,
    ]).pipe(
      takeUntil(this.destroy$),
      withLatestFrom(this.currentDomain$),
      switchMap(([[page, term], domain]) =>
        this.siteContactsService.getCaSiteContacts(domain, page - 1, this.pageSize, term),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.count$ = contacts$.pipe(map((state) => state.totalItems));

    this.assigneeOptions$ = regulators$.pipe(
      map((regulators: RegulatorUserAuthorityInfoDTO[]) =>
        regulators.filter((reg) => reg.authorityStatus === 'ACTIVE'),
      ),
      map((users) =>
        [{ text: 'Unassigned', value: null }].concat(
          users.map((user) => ({ text: this.fullNamePipe.transform(user), value: user.userId })),
        ),
      ),
    );
    this.isEditable$ = contacts$.pipe(map((state) => state.editable));
    this.tableData$ = combineLatest([
      contacts$.pipe(
        map((response) => response.contacts.slice().sort((a, b) => a.accountName.localeCompare(b.accountName))),
      ),
      regulators$,
    ]).pipe(
      map(([contacts, users]) =>
        contacts.map(
          (contact): TableData => ({
            ...contact,
            user: users.find((user) => user.userId === contact.userId),
          }),
        ),
      ),
      tap((contacts) =>
        this.form.setControl(
          'siteContacts',
          this.fb.array(contacts.map(({ accountId, userId }) => this.fb.group({ accountId, userId }))),
        ),
      ),
    );
  }

  onSearch(): void {
    if (this.searchForm.valid) {
      this.router.navigate([], {
        relativeTo: this.route,
        preserveFragment: true,
        queryParams: { term: this.termCtrl.value?.trim() || null, page: null },
        queryParamsHandling: 'merge',
      });
    }
  }

  private get termCtrl(): AbstractControl {
    return this.searchForm.get('term');
  }

  onSave(): void {
    const siteContacts = this.form.get('siteContacts').value;

    this.currentDomain$
      .pipe(
        takeUntil(this.destroy$),
        switchMap((domain) =>
          this.siteContactsService
            .updateCaSiteContacts(domain, siteContacts)
            .pipe(
              catchBadRequest([ErrorCodes.AUTHORITY1003, ErrorCodes.ACCOUNT1004], () =>
                this.businessErrorService.showError(savePartiallyNotFoundSiteContactError),
              ),
            ),
        ),
      )
      .subscribe();
  }
}
