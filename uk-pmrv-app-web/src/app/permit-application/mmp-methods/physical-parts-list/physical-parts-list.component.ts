import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { createPhysicalPartsListForm } from '../mmp-methods';
import { PhysicalPartsTableComponent } from '../shared/physical-parts-table/physical-parts-table.component';

interface ViewModel {
  isEditable: boolean;
}

@Component({
  selector: 'app-mmp-physical-parts-list',
  imports: [PhysicalPartsTableComponent, SharedPermitModule, SharedModule],
  templateUrl: './physical-parts-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PhysicalPartsListComponent {
  readonly state = toSignal(this.store.asObservable());

  readonly vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.state().isEditable;

    return {
      isEditable,
    };
  });

  form = createPhysicalPartsListForm(this.state());

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  addItem() {
    this.router.navigate(['add-part'], { relativeTo: this.route });
  }

  onSubmit() {
    this.router.navigate(['../assign-parts'], { relativeTo: this.route });
  }
}
