import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { iif, map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-site-diagram-summary-template',
  templateUrl: './site-diagram-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SiteDiagramSummaryTemplateComponent implements OnInit {
  @Input() showOriginal = false;
  @Input() cssClass: string;
  task$: Observable<string[]>;
  hasAttachments$: Observable<boolean>;
  files$: Observable<{ id?: string; downloadUrl: string; fileName: string }[]>;

  ngOnInit(): void {
    this.task$ = iif(
      () => this.showOriginal,
      this.store.getOriginalTask('siteDiagrams'),
      this.store.getTask('siteDiagrams'),
    );

    this.hasAttachments$ = this.task$.pipe(map((item) => !!item?.length));

    this.files$ = this.task$.pipe(
      map((task) => this.store.getDownloadUrlFiles(task, 'permitAttachments', this.showOriginal)),
    );
  }

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
