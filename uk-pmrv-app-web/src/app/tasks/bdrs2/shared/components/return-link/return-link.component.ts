import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { map } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-bdrs2-return-link',
  imports: [RouterModule, SharedModule],
  standalone: true,
  template: '<a govukLink [routerLink]="link$ | async">Return to: {{ title }}</a>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BDRS2ReturnLinkComponent {
  @Input() returnLink;
  @Input() title: string = 'Stage 2 baseline data report';

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
