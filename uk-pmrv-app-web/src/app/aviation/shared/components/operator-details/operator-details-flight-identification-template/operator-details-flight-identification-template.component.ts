import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-operator-details-flight-identification-template',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReactiveFormsModule, OperatorDetailsFlightIdentificationTypePipe],
  templateUrl: './operator-details-flight-identification-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsFlightIdentificationTemplateComponent implements OnInit {
  @Input() form: FormGroup<any>;
  @Input() isCorsia = false;
  @Output() readonly submitForm = new EventEmitter<FormGroup<any>>();
  header: string;
  markingsHeader: string;
  ngOnInit(): void {
    this.header = this.isCorsia
      ? 'How do you attribute flights to your organisation under CORSIA?'
      : 'What call sign identification do you use for Air Traffic Control purposes?';
    this.markingsHeader = this.isCorsia
      ? 'List the aeroplane registration markings or codes used'
      : 'List the aircraft registration markings used';
  }
}
