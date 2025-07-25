import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';

import { BehaviorSubject, ReplaySubject } from 'rxjs';

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import {
  AccountContactDTO,
  AccountContactVbInfoDTO,
  AccountContactVbInfoResponse,
  UserAuthorityInfoDTO,
} from 'pmrv-api';

import { UserFullNamePipe } from '../../shared/pipes/user-full-name.pipe';

type TableData = AccountContactVbInfoDTO & { user: UserAuthorityInfoDTO };

@Component({
  selector: 'app-site-contacts',
  templateUrl: './site-contacts.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe],
})
export class SiteContactsComponent implements OnChanges {
  @Input() disabled: boolean;
  @Input() pageSize: number;
  @Input() totalCount: number;
  @Input() verifiers: UserAuthorityInfoDTO[];
  @Input() contacts: AccountContactVbInfoResponse;
  @Output() readonly siteContactChange = new EventEmitter<AccountContactDTO[]>();
  @Output() readonly pageChange = new EventEmitter<number>();
  // eslint-disable-next-line @angular-eslint/no-output-native
  @Output() readonly cancel = new EventEmitter<void>();

  assigneeOptions: GovukSelectOption<string>[];
  tableData: TableData[];
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  page$ = new ReplaySubject<number>(1);
  form = this.fb.group({ siteContacts: this.fb.array([]) });

  columns: GovukTableColumn<TableData>[] = [
    { field: 'accountName', header: 'Permit holding account', isHeader: true },
    { field: 'type', header: 'Type' },
    { field: 'user', header: 'Assigned to' },
  ];

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly fullNamePipe: UserFullNamePipe,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.contacts || changes.verifiers) {
      this.assigneeOptions = [{ text: 'Unassigned', value: null }].concat(
        (this.verifiers ?? [])
          .filter((verifier: UserAuthorityInfoDTO) => verifier.authorityStatus === 'ACTIVE')
          .map((verifier) => ({ text: this.fullNamePipe.transform(verifier), value: verifier.userId })),
      );
      this.tableData =
        this.contacts?.contacts
          .sort((a, b) => a.accountName.localeCompare(b.accountName))
          .map((contact) => ({
            ...contact,
            user: this.verifiers?.find((user) => user.userId === contact.userId),
          })) ?? [];
      this.form.setControl(
        'siteContacts',
        this.fb.array(this.tableData?.map(({ accountId, userId }) => this.fb.group({ accountId, userId })) ?? []),
      );
    }
  }

  submit(): void {
    this.siteContactChange.emit(this.form.get('siteContacts').value);
  }
}
