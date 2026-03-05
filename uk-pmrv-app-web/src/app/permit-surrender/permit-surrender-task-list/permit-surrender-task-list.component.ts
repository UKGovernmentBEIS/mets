import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from '@shared/components/related-actions/request-task-allowed-actions.map';

import { resolveApplyStatus } from '../core/section-status';
import { PermitSurrenderStore } from '../store/permit-surrender.store';

@Component({
  selector: 'app-permit-surrender-task-list',
  templateUrl: './permit-surrender-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class PermitSurrenderTaskListComponent implements OnInit {
  navigationState = { returnUrl: this.router.url };
  readonly actionsForm: UntypedFormGroup = this.fb.group({
    action: [],
  });

  hasRelatedActions$ = this.store.pipe(
    map(
      (state) =>
        (state.assignable && state.userAssignCapable) || hasRequestTaskAllowedActions(state.allowedRequestTaskActions),
    ),
  );
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  canViewSectionDetails$ = this.store.pipe(
    map((state) => state.isEditable || resolveApplyStatus(state) !== 'not started'),
  );

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly fb: UntypedFormBuilder,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.store.isEditable$.pipe(takeUntil(this.destroy$)).subscribe((editable) => {
      if (!editable) {
        this.backLinkService.show();
      }
    });
  }

  submit(): void {
    this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
