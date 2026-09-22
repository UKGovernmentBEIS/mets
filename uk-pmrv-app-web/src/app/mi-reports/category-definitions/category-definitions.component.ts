import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-category-definitions',
  imports: [SharedModule],
  templateUrl: './category-definitions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoryDefinitionsComponent {}
