import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { NotificationTemplateDTO } from 'pmrv-api';

@Component({
  selector: 'app-email-template-overview',
  templateUrl: './email-template-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailTemplateOverviewComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  emailTemplate$: Observable<NotificationTemplateDTO> = this.route.data.pipe(map((x) => x?.emailTemplate));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
