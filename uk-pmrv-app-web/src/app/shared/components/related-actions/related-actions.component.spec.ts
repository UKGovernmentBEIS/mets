import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Route } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of } from 'rxjs';

import { RelatedActionsComponent } from '@shared/components/related-actions/related-actions.component';
import { BasePage } from '@testing';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

import { SharedModule } from '../../shared.module';

describe('RelatedActionsComponent', () => {
  let component: RelatedActionsComponent;
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-related-actions
        [taskId$]="taskId$"
        [isAssignable$]="isAssignable$"
        [allowedActions$]="allowedActions$"
      ></app-related-actions>
    `,
  })
  class TestComponent {
    taskId$: Observable<number>;
    isAssignable$: Observable<boolean>;
    allowedActions$: Observable<Array<RequestTaskActionProcessDTO['requestTaskActionType']>>;
  }

  class Page extends BasePage<TestComponent> {
    get links() {
      return this.queryAll<HTMLLinkElement>('li > a');
    }
  }

  const setupTestingModule = async (withChangeAssigneeRoute = false) => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [RelatedActionsComponent, TestComponent],
      providers: [{ provide: ActivatedRoute, useValue: constructRoute(withChangeAssigneeRoute) }],
    }).compileComponents();
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    testComponent.isAssignable$ = of(true);
    testComponent.taskId$ = of(1);
    testComponent.allowedActions$ = of([]);
    component = fixture.debugElement.query(By.directive(RelatedActionsComponent)).componentInstance;
    page = new Page(fixture);
  };

  it('should create', async () => {
    await setupTestingModule();
    createComponent();
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should display the links', async () => {
    await setupTestingModule();
    createComponent();
    fixture.detectChanges();

    expect(page.links.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/tasks/1/change-assignee', 'Reassign task'],
    ]);
  });

  it('should display the links with actions', async () => {
    await setupTestingModule();
    createComponent();

    testComponent.allowedActions$ = of([
      'RFI_SUBMIT',
      'RDE_SUBMIT',
      'PERMIT_ISSUANCE_RECALL_FROM_AMENDS',
      'PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS',
      'AER_SAVE_APPLICATION',
      'AVIATION_ACCOUNT_CLOSURE_CANCEL_APPLICATION',
    ]);
    fixture.detectChanges();

    expect(page.links.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/tasks/1/change-assignee', 'Reassign task'],
      ['http://localhost/rfi/1/questions', 'Request for information'],
      ['http://localhost/rde/1/extend-determination', 'Request deadline extension'],
      ['http://localhost/', 'Recall the permit'],
      ['http://localhost/', 'Recall your response'],
      ['http://localhost/aviation/tasks/1/cancel', 'Cancel this task'],
    ]);
  });

  it('should display the links with actions in aviation', async () => {
    await setupTestingModule(true);
    createComponent();

    component.baseUrl = '/aviation';
    testComponent.allowedActions$ = of(['RFI_SUBMIT', 'RDE_SUBMIT']);
    fixture.detectChanges();

    expect(page.links.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/', 'Reassign task'],
      ['http://localhost/aviation/rfi/1/questions', 'Request for information'],
      ['http://localhost/aviation/rde/1/extend-determination', 'Request deadline extension'],
    ]);
  });
});

function constructRoute(withChangeAssignee = false): Partial<ActivatedRoute> {
  return {
    snapshot: {
      get routeConfig() {
        return { path: '' };
      },
      get parent(): any {
        return {
          get routeConfig(): Route | null {
            return {
              path: 'parent',
              children: [{ path: withChangeAssignee ? 'change-assignee' : '' }],
            };
          },
        };
      },
    } as any,
  };
}
