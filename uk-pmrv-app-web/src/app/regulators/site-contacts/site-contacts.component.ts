import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

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

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import {
  AccountContactInfoDTO,
  CaSiteContactsService,
  RegulatorAuthoritiesService,
  RegulatorUserAuthorityInfoDTO,
} from 'pmrv-api';

import { savePartiallyNotFoundSiteContactError } from '../errors/business-error';

type TableData = AccountContactInfoDTO & { user: RegulatorUserAuthorityInfoDTO; type: string };

@Component({
  selector: 'app-site-contacts',
  templateUrl: './site-contacts.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe, DestroySubject],
})
export class SiteContactsComponent implements OnInit {
  page$ = new ReplaySubject<number>(1);
  count$: Observable<number>;
  columns: GovukTableColumn<TableData>[] = [
    { field: 'accountName', header: 'Permit holding account', isHeader: true },
    { field: 'type', header: 'Type' },
    { field: 'user', header: 'Assigned to' },
  ];
  tableData$: Observable<TableData[]>;
  isEditable$: Observable<boolean>;
  readonly pageSize = 50;
  form = this.fb.group({ siteContacts: this.fb.array([]) });
  assigneeOptions$: Observable<GovukSelectOption<string>[]>;
  refresh$ = new Subject<void>();
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly siteContactsService: CaSiteContactsService,
    private readonly fullNamePipe: UserFullNamePipe,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    const activatedTab$ = this.route.fragment.pipe(filter((fragment) => fragment === 'site-contacts'));

    const regulators$ = merge(activatedTab$, this.refresh$).pipe(
      switchMap(() => this.regulatorAuthoritiesService.getCaRegulators()),
      map((state) => state.caUsers),
      shareReplay({ bufferSize: 1, refCount: false }),
    );

    const contacts$ = combineLatest([
      merge(this.refresh$.pipe(switchMap(() => this.page$)), this.page$.pipe(distinctUntilChanged())),
      activatedTab$,
    ]).pipe(
      takeUntil(this.destroy$),
      withLatestFrom(this.currentDomain$),
      switchMap(([[page], domain]) => this.siteContactsService.getCaSiteContacts(domain, page - 1, this.pageSize)),
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
      this.currentDomain$,
    ]).pipe(
      map(([contacts, users, domain]) =>
        contacts.map(
          (contact): TableData => ({
            ...contact,
            user: users.find((user) => user.userId === contact.userId),
            type: domain[0] + domain.slice(1).toLocaleLowerCase(),
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
