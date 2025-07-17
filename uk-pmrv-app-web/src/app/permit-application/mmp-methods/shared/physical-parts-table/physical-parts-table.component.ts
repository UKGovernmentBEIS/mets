import { ChangeDetectionStrategy, Component, computed, Input, OnInit, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { createPhysicalPartsListForm } from '@permit-application/mmp-methods/mmp-methods';
import { noPhysicalPartsValidator } from '@permit-application/mmp-methods/physical-parts-list/physical-parts-list';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { GovukTableColumn } from 'govuk-components';

import { MethodTaskConnection } from 'pmrv-api';

interface ViewModel {
  physicalPartsColumns: Array<GovukTableColumn>;
  physicalParts: Array<MethodTaskConnection>;
  isEditable: boolean;
}

@Component({
  selector: 'app-mmp-physical-parts-table',
  standalone: true,
  imports: [SharedModule, SharedPermitModule, RouterLink],
  templateUrl: './physical-parts-table.component.html',
  styleUrl: './physical-parts-table.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PhysicalPartsTableComponent implements OnInit {
  @Input() isSummaryPage: boolean;
  @Input() isEditable: boolean;

  state = toSignal(this.store.asObservable());
  digitizedPlan = computed(() => this.state().permit.monitoringMethodologyPlans?.digitizedPlan);
  form = createPhysicalPartsListForm(this.state());
  removeLink: Array<string> = [];

  vm: Signal<ViewModel> = computed(() => {
    const physicalParts = this.digitizedPlan()?.methodTask?.connections;
    const isEditable = this.isEditable ?? this.state().isEditable;

    return {
      physicalPartsColumns: [
        { field: 'itemName', header: 'Physical part of the installation or unit' },
        { field: 'subInstallations', header: 'Emission sources' },
        { field: 'itemId', header: '' },
        { field: 'remove', header: '', widthClass: 'govuk-input--width-20', alignRight: true },
        { field: 'change', header: '', widthClass: 'govuk-input--width-20', alignRight: true },
      ],
      physicalParts,
      isEditable,
    };
  });

  constructor(
    private readonly fb: UntypedFormBuilder,
    readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  ngOnInit(): void {
    this.removeLink = this.isSummaryPage ? ['../', 'remove'] : ['remove'];

    if (!this.form) {
      this.form = this.fb.group(
        {},
        {
          validators: [noPhysicalPartsValidator(this.digitizedPlan().methodTask?.connections)],
        },
      );
    }
  }
}
