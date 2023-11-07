import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { DreService } from '../../../../core/dre.service';

@Component({
  selector: 'app-information-source-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  index = this.route.snapshot.paramMap.get('index');

  constructor(
    readonly dreService: DreService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  delete(): void {
    this.dreService.dre$
      .pipe(
        first(),
        switchMap((dre) => {
          return this.dreService.saveDre(
            {
              informationSources: dre?.informationSources?.filter((_, i) => i !== Number(this.index)),
            },
            false,
          );
        }),
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
