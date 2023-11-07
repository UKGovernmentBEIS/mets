import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, distinctUntilChanged, map, switchMap } from 'rxjs';

import { AccountNoteDto, AccountNotesService } from 'pmrv-api';

@Component({
  selector: 'app-account-notes',
  templateUrl: './account-notes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountNotesComponent {
  @Input() currentTab: string;

  readonly pageSize = 10;
  page$ = new BehaviorSubject<number>(1);
  accountNotes$ = combineLatest([
    this.route.paramMap.pipe(map((parameters) => +parameters.get('accountId'))),
    this.page$.pipe(distinctUntilChanged()),
  ]).pipe(
    switchMap(([accountId, page]) => this.accountNotesService.getNotesByAccountId(accountId, page - 1, this.pageSize)),
  );

  constructor(private readonly accountNotesService: AccountNotesService, private readonly route: ActivatedRoute) {}

  getDownloadUrlFiles(note: AccountNoteDto): { downloadUrl: string; fileName: string }[] {
    const files = note.payload.files || {};

    return (
      Object.keys(files)?.map((uuid) => ({
        downloadUrl: `./file-download/${uuid}`,
        fileName: files[uuid],
      })) ?? []
    );
  }
}
