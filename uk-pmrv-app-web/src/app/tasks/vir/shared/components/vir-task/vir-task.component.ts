import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { VirService } from '@tasks/vir/core/vir.service';

@Component({
  selector: 'app-vir-task',
  template: `
    <app-page-heading>{{ heading }}</app-page-heading>
    <ng-content></ng-content>
    <a govukLink [routerLink]="returnToLink">Return to: {{ returnLinkText | async }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VirTaskComponent {
  @Input() heading: string;
  @Input() returnToLink = '..';

  returnLinkText: Observable<string> = this.virService.payload$.pipe(
    map((payload) => {
      switch (payload?.payloadType) {
        case 'VIR_APPLICATION_SUBMIT_PAYLOAD': {
          return 'Verifier Improvement Report';
        }
        case 'VIR_APPLICATION_REVIEW_PAYLOAD': {
          return 'Review verifier improvement report';
        }
        case 'VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD': {
          return 'Respond to regulator comments';
        }
        default: {
          return '';
        }
      }
    }),
  );

  constructor(private readonly backLinkService: BackLinkService, private readonly virService: VirService) {
    this.backLinkService.show();
  }
}
