import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-additional-info',
  templateUrl: './additional-info.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalInfoComponent {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((x) => x?.groupKey));

  files$ = this.store.getTask('additionalDocuments').pipe(
    filter((item) => !!item),
    map((item) => this.store?.getDownloadUrlFiles(item.documents)),
  );

  originalFiles$ = this.store.getOriginalTask('additionalDocuments').pipe(
    filter((item) => !!item),
    map((item) => this.store.getDownloadUrlFiles(item.documents, 'permitAttachments', true)),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
