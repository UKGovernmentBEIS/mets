import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { TagColor } from './tag-color.type';

@Component({
  selector: 'govuk-tag',
  standalone: false,
  templateUrl: './tag.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TagComponent {
  @Input() color: TagColor;
}
