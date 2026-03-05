import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { DreService } from '../../../core/dre.service';
import { informationSourceFormProvider } from './information-source-form.provider';

@Component({
  selector: 'app-information-source',
  templateUrl: './information-source.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [informationSourceFormProvider],
})
export class InformationSourceComponent {
  private readonly nextWizardStep = 'information-sources';

  index = this.route.snapshot.paramMap.get('index');
  createMode = this.index === null;

  constructor(
    @Inject(DRE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly dreService: DreService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
    } else {
      this.dreService.dre$
        .pipe(
          first(),
          switchMap((dre) => {
            const informationSources = this.createMode
              ? [...(dre?.informationSources ?? []), this.form.value.informationSource]
              : dre?.informationSources?.map((informationSource, idx) =>
                  idx === Number(this.index) ? this.form.value.informationSource : informationSource,
                );
            return this.dreService.saveDre(
              {
                informationSources,
              },
              false,
            );
          }),
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../..', this.nextWizardStep], { relativeTo: this.route }));
    }
  }
}
