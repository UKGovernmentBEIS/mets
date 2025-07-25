import { ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { RequestActionDTO } from 'pmrv-api';

@Component({
  selector: 'app-action-layout',
  templateUrl: './action-layout.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActionLayoutComponent implements OnInit {
  @Input() header: string;
  @Input() requestAction: RequestActionDTO;
  @Input() customContentTemplate: TemplateRef<any>;

  navigationState = { returnUrl: this.router.url };

  constructor(
    private readonly backService: BackLinkService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.backService.show();
  }
}
