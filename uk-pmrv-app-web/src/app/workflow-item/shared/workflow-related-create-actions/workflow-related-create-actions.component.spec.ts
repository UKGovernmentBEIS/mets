import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { Observable, of } from 'rxjs';

import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { RequestCreateActionProcessDTO, RequestItemsService, RequestsService } from 'pmrv-api';

import { WorkflowRelatedCreateActionsComponent } from './workflow-related-create-actions.component';

describe('WorkflowRelatedCreateActionsComponent', () => {
  let component: WorkflowRelatedCreateActionsComponent;
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  const requestService = mockClass(RequestsService);
  const requestItemsService = mockClass(RequestItemsService);

  @Component({
    template: `
      <app-workflow-related-create-actions
        [accountId$]="accountId$"
        [requestId$]="requestId$"
        [requestCreateActionsTypes$]="requestCreateActionsTypes$"></app-workflow-related-create-actions>
    `,
  })
  class TestComponent {
    accountId$: Observable<number>;
    requestId$: Observable<string>;
    requestCreateActionsTypes$: Observable<RequestCreateActionProcessDTO['requestCreateActionType'][]>;
  }

  class Page extends BasePage<TestComponent> {
    get links() {
      return this.queryAll<HTMLLinkElement>('li > a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [WorkflowRelatedCreateActionsComponent, TestComponent],
      providers: [
        provideRouter([]),
        { provide: RequestsService, useValue: requestService },
        { provide: RequestItemsService, useValue: requestItemsService },
        ItemLinkPipe,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    testComponent.accountId$ = of(1);
    testComponent.requestId$ = of('AEM00001-2022');
    testComponent.requestCreateActionsTypes$ = of([]);
    component = fixture.debugElement.query(By.directive(WorkflowRelatedCreateActionsComponent)).componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    page = new Page(fixture);
  });

  afterEach(async () => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should not display any links', () => {
    fixture.detectChanges();
    expect(page.links).toEqual([]);
  });

  it('when AER action is available it should display it and when click to navigate to aer reinitialize page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    testComponent.requestCreateActionsTypes$ = of(['AER']);
    fixture.detectChanges();

    expect(page.links.map((el) => el.textContent.trim())).toEqual(['Return to operator for changes']);

    page.links[0].click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['aer-reinitialize'], { relativeTo: activatedRoute });
  });

  it('when DRE action is available it should display it and when click to navigate to submit task page', () => {
    requestService.processRequestCreateAction.mockReturnValueOnce(of({ requestId: '1234' }));
    requestItemsService.getItemsByRequest.mockReturnValueOnce(
      of({
        items: [
          {
            requestId: '123',
            requestType: 'DRE',
            taskId: 4,
            taskType: 'DRE_APPLICATION_SUBMIT',
          },
        ],
      }),
    );
    const navigateSpy = jest.spyOn(router, 'navigate');

    testComponent.requestCreateActionsTypes$ = of(['DRE']);
    fixture.detectChanges();

    expect(page.links.map((el) => el.textContent.trim())).toEqual(['Determine reportable emissions']);

    page.links[0].click();
    fixture.detectChanges();

    expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
    expect(requestService.processRequestCreateAction).toHaveBeenCalledWith(
      {
        requestCreateActionType: 'DRE',
        requestCreateActionPayload: {
          payloadType: 'REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD',
          requestId: 'AEM00001-2022',
        },
      },
      String(1),
    );

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['/tasks', 4, 'dre', 'submit']);
  });

  it('when DOE action is available it should display it and when click to navigate to submit task page', () => {
    requestService.processRequestCreateAction.mockReturnValueOnce(of({ requestId: '1234' }));
    requestItemsService.getItemsByRequest.mockReturnValueOnce(
      of({
        items: [
          {
            requestId: '123',
            requestType: 'AVIATION_DOE_CORSIA',
            taskId: 4,
            taskType: 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT',
          },
        ],
      }),
    );
    const navigateSpy = jest.spyOn(router, 'navigate');

    testComponent.requestCreateActionsTypes$ = of(['AVIATION_DOE_CORSIA']);
    fixture.detectChanges();

    expect(page.links.map((el) => el.textContent.trim())).toEqual(['Initiate estimation of emissions']);

    page.links[0].click();
    fixture.detectChanges();

    expect(requestService.processRequestCreateAction).toHaveBeenCalledTimes(1);
    expect(requestService.processRequestCreateAction).toHaveBeenCalledWith(
      {
        requestCreateActionType: 'AVIATION_DOE_CORSIA',
        requestCreateActionPayload: {
          payloadType: 'REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD',
          requestId: 'AEM00001-2022',
        },
      },
      String(1),
    );

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['/aviation/tasks', 4]);
  });
});
