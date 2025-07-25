import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { reviewRequestTaskTypes } from '../../../shared/utils/permit';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { createCellAndAnodeTypes, typesFormProvider } from './types-form.provider';

@Component({
  selector: 'app-types',
  templateUrl: './types.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [typesFormProvider, DestroySubject],
})
export class TypesComponent implements AfterViewInit {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  private cellAndAnodeTypesLength = this.cellAndAnodeTypesFormArray.length;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get cellAndAnodeTypesFormArray(): UntypedFormArray {
    return this.form.get('cellAndAnodeTypes') as UntypedFormArray;
  }

  ngAfterViewInit(): void {
    this.removeButtons.changes.pipe(takeUntil(this.destroy$)).subscribe((buttons) => {
      if (buttons.length > this.cellAndAnodeTypesLength) {
        buttons.last.nativeElement.focus();
      }
      this.cellAndAnodeTypesLength = buttons.length;
    });
  }
  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.store.postTask(data.taskKey, this.form.get('cellAndAnodeTypes').value, true, data.statusKey),
        ),
        this.pendingRequest.trackRequest(),
        switchMap(() => this.store),
        first(),
      )
      .subscribe((state) =>
        this.router.navigate(
          [reviewRequestTaskTypes.includes(state.requestTaskType) ? '../../review/pfc' : 'summary'],
          { relativeTo: this.route, state: { notification: true } },
        ),
      );
  }

  addCellAnodeType(): void {
    this.cellAndAnodeTypesFormArray.push(createCellAndAnodeTypes());
    this.wizardStepComponent.isSummaryDisplayedSubject
      .pipe(
        first(),
        filter((value) => value),
      )
      .subscribe(() =>
        this.cellAndAnodeTypesFormArray.at(this.cellAndAnodeTypesFormArray.length - 1).markAllAsTouched(),
      );
  }
}
