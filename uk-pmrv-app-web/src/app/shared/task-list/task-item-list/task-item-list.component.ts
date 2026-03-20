import { ChangeDetectionStrategy, Component, HostBinding, Input } from '@angular/core';

import { TaskItem } from '../task-list.interface';

@Component({
  selector: 'ul[app-task-item-list]',
  standalone: false,
  template: `
    <li
      app-task-item
      *ngFor="let task of taskItems"
      [link]="task.link"
      [linkText]="task.linkText"
      [status]="task.status"></li>
    <ng-content></ng-content>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskItemListComponent {
  @Input() taskItems: TaskItem<any>[];

  // eslint-disable-next-line @angular-eslint/prefer-host-metadata-property
  @HostBinding('class.app-task-list__items') readonly taskListItems = true;
}
