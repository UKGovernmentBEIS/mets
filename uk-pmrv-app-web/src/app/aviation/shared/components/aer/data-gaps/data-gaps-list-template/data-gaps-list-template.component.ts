import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerDataGap } from 'pmrv-api';

@Component({
  selector: 'app-data-gaps-list-template',
  imports: [GovukComponentsModule, RouterLinkWithHref, NgIf, NgFor],
  templateUrl: './data-gaps-list-template.component.html',
  styleUrl: './data-gaps-list-template.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsListTemplateComponent {
  @Input() dataGaps: AviationAerDataGap[];
  @Input() isEditable = false;
  @Input() queryParams: Params = {};

  @Output() readonly removeDataGapEvent = new EventEmitter<number>();

  protected onRemoveDataGap(index: number) {
    this.removeDataGapEvent.emit(index);
  }
}
