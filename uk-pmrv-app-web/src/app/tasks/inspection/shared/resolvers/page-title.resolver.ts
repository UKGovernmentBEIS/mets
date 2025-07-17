import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

export const pageTitleResolver: ResolveFn<string> = () => {
  {
    const store = inject(CommonTasksStore);
    const inspectionService = inject(InspectionService);

    const year = inspectionService.auditYear;
    const itemNamePipe = new ItemNamePipe();
    const requestTasktype = store.getState()?.requestTaskItem?.requestTask.type;

    return itemNamePipe.transform(requestTasktype, year);
  }
};
