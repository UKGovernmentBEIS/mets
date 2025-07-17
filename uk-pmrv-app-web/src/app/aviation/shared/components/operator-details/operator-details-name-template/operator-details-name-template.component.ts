import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-operator-details-name-template',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule],
  templateUrl: './operator-details-name-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsNameTemplateComponent implements OnInit {
  @Input() form: FormGroup<any>;
  @Input() isCorsia = false;
  @Output() readonly submitForm = new EventEmitter<FormGroup<any>>();

  label = 'Aircraft operator name';

  ngOnInit(): void {
    if (this.isCorsia) this.label = 'Aeroplane operator name';
  }
}
