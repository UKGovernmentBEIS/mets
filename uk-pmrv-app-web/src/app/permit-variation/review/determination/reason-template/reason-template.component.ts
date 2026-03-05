import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../permit-application/shared/permit-task-form.token';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { PermitVariationStore } from '../../../store/permit-variation.store';
import { reasonTemplatesMap } from './reason-template.type';
import { reasonTemplateFormProvider } from './reason-template-form.provider';

@Component({
  selector: 'app-reason-template',
  templateUrl: './reason-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reasonTemplateFormProvider],
})
export class ReasonTemplateComponent implements PendingRequest, OnInit {
  determination$ = this.store.getDeterminationType$();

  reasonTemplateOptions = Object.keys(reasonTemplatesMap);
  reasonTemplatesMap = reasonTemplatesMap;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitVariationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../log-changes'], { relativeTo: this.route });
    } else {
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postDetermination(
              {
                ...state.determination,
                reasonTemplate: this.form.value.reasonTemplate,
                reasonTemplateOtherSummary:
                  this.form.value.reasonTemplate === 'OTHER' ? this.form.value.reasonTemplateOtherSummary : null,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../log-changes'], { relativeTo: this.route }));
    }
  }
}
