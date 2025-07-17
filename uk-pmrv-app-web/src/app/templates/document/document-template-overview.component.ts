import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { DocumentTemplateDTO } from 'pmrv-api';

@Component({
  selector: 'app-document-template-overview',
  templateUrl: './document-template-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentTemplateOverviewComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  documentTemplate$: Observable<DocumentTemplateDTO> = this.route.data.pipe(map((x) => x?.documentTemplate));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
