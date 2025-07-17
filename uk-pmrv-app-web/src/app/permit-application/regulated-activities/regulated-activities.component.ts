import { ChangeDetectionStrategy, Component, computed, Inject, OnInit, Signal, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import {
  activityGroupMap,
  activityHintMap,
  formGroupOptions,
  RegulatedActivitiesFormGroup,
  unitOptions,
} from '@shared/components/regulated-activities/regulated-activities-form-options';

import { RegulatedActivity } from 'pmrv-api';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { originalOrder } from '../../shared/keyvalue-order';
import { IdGeneratorService } from '../../shared/services/id-generator.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { regulatedActivitiesFormProvider } from './regulated-activities-form.provider';

@Component({
  selector: 'app-regulated-activities',
  templateUrl: './regulated-activities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, regulatedActivitiesFormProvider, IdGeneratorService],
})
export class RegulatedActivitiesComponent extends SectionComponent implements PendingRequest, OnInit {
  readonly originalOrder = originalOrder;
  private readonly stateFeatures = toSignal(this.store.pipe(map((state) => state.features)));

  activityGroups: Signal<{
    [K in keyof RegulatedActivitiesFormGroup]: RegulatedActivity['type'][];
  }> = computed(() => {
    const features = this.stateFeatures();

    let groupOptions: {
      [K in keyof RegulatedActivitiesFormGroup]: RegulatedActivity['type'][];
    };

    if (features?.['co2-venting.permit-workflows.enabled'] && !this.hideUpstream()) {
      groupOptions = {
        ...formGroupOptions,
        COMBUSTION_GROUP: [...formGroupOptions.COMBUSTION_GROUP, 'UPSTREAM_GHG_REMOVAL'],
      };
    } else {
      groupOptions = formGroupOptions;
    }

    if (!features?.['wastePermitEnabled']) {
      if (groupOptions.WASTE_GROUP) {
        delete groupOptions.WASTE_GROUP;
      }
    }

    return groupOptions;
  });

  uncheckedRegulatedActivities: RegulatedActivity[];
  isDeleteConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);
  activityGroupMap = activityGroupMap;
  unitOptions = unitOptions;
  activityHintMap = activityHintMap;
  hideUpstream = signal(false);

  private newRegulatedActivities: RegulatedActivity[];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly idGeneratorService: IdGeneratorService,
    private readonly destroy$: DestroySubject,
  ) {
    super(store, router, route);
  }

  ngOnInit(): void {
    this.form
      .get('COMBUSTION_GROUP')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((groupValue: RegulatedActivitiesFormGroup['COMBUSTION_GROUP']) => {
        if (!groupValue?.includes('COMBUSTION')) {
          this.hideUpstream.set(true);
          if (groupValue?.includes('UPSTREAM_GHG_REMOVAL')) {
            this.form.get('COMBUSTION_GROUP').patchValue(null);
          }
        } else {
          this.hideUpstream.set(false);
        }
      });
    this.form.get('COMBUSTION_GROUP').updateValueAndValidity();
  }

  onSubmit(): void {
    this.store
      .getTask('regulatedActivities')
      .pipe(
        first(),
        map((regulatedActivities) => {
          this.newRegulatedActivities = this.mapToStore(regulatedActivities);
          this.uncheckedRegulatedActivities = regulatedActivities.filter((currentRegAct) =>
            this.newRegulatedActivities.every((newRegAct) => newRegAct.type !== currentRegAct.type),
          );

          if (this.uncheckedRegulatedActivities.length === 0) {
            this.confirmSubmit();
          } else {
            this.isDeleteConfirmationDisplayed$.next(true);
          }
        }),
      )
      .subscribe();
  }

  confirmSubmit(): void {
    this.store
      .postTask('regulatedActivities', this.newRegulatedActivities, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.navigateSubmitSection('summary', 'details'));
  }

  private mapToStore(regulatedActivities: RegulatedActivity[]): RegulatedActivity[] {
    return Object.entries<RegulatedActivity['type'][]>(this.form.value)
      .filter(([key, value]) => key.endsWith('_GROUP') && value?.length > 0)
      .map(([, value]) => value)
      .reduce(
        (result: RegulatedActivity[], types) => [
          ...result,
          ...types.map((type) => {
            const capacity = this.form.value[`${type}_CAPACITY`];
            const capacityUnit = this.form.value[`${type}_CAPACITY_UNIT`];
            const id =
              regulatedActivities.find((activity) => activity.type === type)?.id ??
              this.idGeneratorService.generateId();

            return {
              id,
              capacity,
              capacityUnit,
              type,
            } as RegulatedActivity;
          }),
        ],
        [],
      );
  }
}
