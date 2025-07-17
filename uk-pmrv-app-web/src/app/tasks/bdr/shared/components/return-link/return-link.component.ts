import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { map } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-bdr-return-link',
  template: '<a govukLink [routerLink]="link$ | async">Return to: {{ title }}</a>',
  standalone: true,
  imports: [RouterModule, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BDRReturnLinkComponent {
  @Input() returnLink;
  @Input() title: string = 'Baseline data report';

  link$ = this.route.url.pipe(
    map((url) => {
      const isIncludedInUrl = url.some(
        (segment) => segment.path.includes('summary') || segment.path.includes('answers'),
      );
      return this.returnLink ? this.returnLink : isIncludedInUrl ? '../..' : '..';
    }),
  );

  constructor(private readonly route: ActivatedRoute) {}
}
