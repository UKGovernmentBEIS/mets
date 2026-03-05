import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { FollowUpActionTypePipe } from '@tasks/inspection/shared/pipes/follow-up-action-type.pipe';

import { FollowUpAction, FollowUpActionResponse } from 'pmrv-api';

@Component({
  selector: 'app-follow-up-item',
  templateUrl: './follow-up-item.component.html',
  standalone: true,
  imports: [SharedModule, RouterLink, FollowUpActionTypePipe],
  styles: `
    .govuk-grid-column-one-quarter {
      text-align: right;
      font-size: 1.1875rem;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpItemComponent {
  @Input() followUpAction: FollowUpAction | FollowUpActionResponse;
  @Input() index: number;
  @Input() isEditable: boolean;
  @Input() files: AttachedFile[];
  @Input() hideHeading = false;
  @Input() extraTitle: string = null;

  @Output() readonly removeFollowUpItem = new EventEmitter<number>();

  onRemoveFollowUpItem(index: number) {
    this.removeFollowUpItem.emit(index);
  }
}
