import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, distinctUntilChanged, map, switchMap } from 'rxjs';

import { RequestNoteDto, RequestNotesService } from 'pmrv-api';

@Component({
  selector: 'app-request-notes',
  templateUrl: './request-notes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestNotesComponent {
  @Input() currentTab: string;

  readonly pageSize = 10;
  page$ = new BehaviorSubject<number>(1);
  requestId$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('request-id')));

  requestNotes$ = combineLatest([this.requestId$, this.page$.pipe(distinctUntilChanged())]).pipe(
    switchMap(([requestId, page]) => this.requestNotesService.getNotesByRequestId(requestId, page - 1, this.pageSize)),
  );

  constructor(private readonly requestNotesService: RequestNotesService, private readonly route: ActivatedRoute) {}

  getDownloadUrlFiles(note: RequestNoteDto): { downloadUrl: string; fileName: string }[] {
    const files = note.payload.files || {};

    return (
      Object.keys(files)?.map((uuid) => ({
        downloadUrl: `./file-download/${uuid}`,
        fileName: files[uuid],
      })) ?? []
    );
  }
}
