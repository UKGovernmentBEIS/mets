import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { InstallationConnection } from 'pmrv-api';

@Component({
  selector: 'app-connection-list-summary-template',
  templateUrl: './connection-list-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConnectionListSummaryTemplateComponent {
  @Input() connections: InstallationConnection[];
  @Input() isEditable: boolean;
  @Input() baseLink: string = '';
  @Input() hasBottomBorder = true;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
