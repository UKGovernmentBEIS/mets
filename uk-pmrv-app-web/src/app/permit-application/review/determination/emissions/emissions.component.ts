import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map, switchMap, take } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { WizardStepComponent } from '../../../../shared/wizard/wizard-step.component';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { createAnotherEmissionsTarget, emissionsFormProvider } from './emissions-form.provider';

@Component({
  selector: 'app-emissions',
  templateUrl: './emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionsFormProvider],
})
export class EmissionsComponent implements OnInit, PendingRequest {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  determination$ = this.store.getDeterminationType$();
  determinationHeader = this.store.getDeterminationHeader();

  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get annualEmissionsTargets(): UntypedFormArray {
    return this.form.get('annualEmissionsTargets') as UntypedFormArray;
  }

  onContinue(): void {
    const annualEmissionsTargets = {};
    this.form.value.annualEmissionsTargets.forEach(
      (target) => (annualEmissionsTargets[target.year] = Number(target.emissions)),
    );

    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.store.postDetermination(
            {
              ...state.determination,
              annualEmissionsTargets: annualEmissionsTargets,
            },
            false,
          ),
        ),
        this.pendingRequest.trackRequest(),
        switchMap(() => this.store),
        take(1),
      )
      .subscribe((state) => this.router.navigate([`../${this.getNextStepUrl(state)}`], { relativeTo: this.route }));
  }

  addOtherAnnualEmissionsTarget(): void {
    this.annualEmissionsTargets.push(createAnotherEmissionsTarget());
    this.wizardStepComponent.isSummaryDisplayedSubject
      .pipe(
        first(),
        filter((value) => value),
      )
      .subscribe(() => this.annualEmissionsTargets.at(this.annualEmissionsTargets.length - 1).markAllAsTouched());
  }

  private getNextStepUrl(state: PermitApplicationState): string {
    return state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW'
      ? 'log-changes'
      : state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'
        ? 'reason-template'
        : 'answers';
  }
}
