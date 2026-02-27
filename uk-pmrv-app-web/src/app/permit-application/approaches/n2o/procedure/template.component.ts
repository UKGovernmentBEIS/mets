import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-template',
  standalone: false,
  templateUrl: './template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TemplateComponent {
  @Input() taskKey: string;
}
