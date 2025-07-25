import { ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';

import { filter, first, Observable, switchMap } from 'rxjs';

import { RequestActionDTO } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';

@Component({
  selector: 'app-base-action-container-component[expectedActionType][header]',
  template: `
    <ng-container *ngIf="dataLoaded">
      <app-action-layout
        *ngIf="dataLoaded"
        [header]="header"
        [requestAction]="requestAction$ | async"
        [customContentTemplate]="customContentTemplate"></app-action-layout>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaseActionContainerComponent implements OnInit {
  @Input() header: string;
  @Input() expectedActionType: RequestActionDTO['type'][];
  @Input() actionsTemplate: TemplateRef<any>;
  @Input() customContentTemplate: TemplateRef<any>;

  requestAction$: Observable<RequestActionDTO> = this.commonActionsStore.requestAction$;
  dataLoaded = false;

  constructor(
    private readonly router: Router,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}

  ngOnInit(): void {
    this.commonActionsStore.storeInitialized$
      .pipe(
        filter((storeInitialized) => !!storeInitialized),
        switchMap(() => this.commonActionsStore.requestAction$),
        first(),
      )
      .subscribe((requestActionItem) => {
        if (!requestActionItem) {
          this.router.navigate(['/error', '404']).then(() => console.error('No Request Action Item provided.'));
        } else if (this.expectedActionType.every((t) => t !== requestActionItem.type)) {
          throw Error('Invalid Request Task Item for given id.');
        } else {
          this.dataLoaded = true;
        }
      });
  }
}
