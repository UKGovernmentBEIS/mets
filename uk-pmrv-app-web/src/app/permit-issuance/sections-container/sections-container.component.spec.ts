import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { MockType } from '../../../testing';
import { mockState } from '../../permit-application/testing/mock-state';
import { SharedModule } from '../../shared/shared.module';
import { PermitIssuanceStore } from '../store/permit-issuance.store';
import { SectionsContainerComponent } from './sections-container.component';

describe('SectionsContainerComponent', () => {
  let component: SectionsContainerComponent;
  let fixture: ComponentFixture<SectionsContainerComponent>;
  let hostElement: HTMLElement;

  let store: PermitIssuanceStore;

  const requestItemsService: MockType<RequestItemsService> = {
    getItemsByRequest: jest.fn().mockReturnValue(
      of({
        items: [
          {
            requestType: 'PERMIT_ISSUANCE',
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
            taskId: 100,
          },
          {
            requestType: 'PERMIT_ISSUANCE',
            taskType: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
            taskId: mockState.requestTaskId,
          },
        ],
        totalItems: 2,
      }),
    ),
  };

  const requestActionsService: MockType<RequestActionsService> = {
    getRequestActionsByRequestId: jest.fn().mockReturnValue(
      of([
        {
          id: 1,
          creationDate: '2020-08-25 10:36:15.189643',
          submitter: 'Operator',
          type: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED',
        },
        {
          id: 2,
          creationDate: '2020-08-26 10:36:15.189643',
          submitter: 'Regulator',
          type: 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED',
        },
      ]),
    ),
  };

  @Component({
    selector: 'app-sections',
    template: `permit sections`,
  })
  class MockPermitSectionsComponent {}

  const createComponent = () => {
    fixture = TestBed.createComponent(SectionsContainerComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SectionsContainerComponent, MockPermitSectionsComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState({
      ...mockState,
      requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND'],
    });

    createComponent();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display header', () => {
    expect(hostElement.querySelector('app-page-heading h1').textContent).toContain('Apply for a GHGE permit');
  });

  it('should filter related tasks and display request actions', () => {
    expect(hostElement.querySelectorAll('app-timeline-item').length).toEqual(2);
    expect(hostElement.querySelectorAll('app-related-tasks').length).toBeTruthy();
    expect(hostElement.querySelectorAll('app-related-actions').length).toBeTruthy();
    expect(requestItemsService.getItemsByRequest).toHaveBeenCalledTimes(1);
    expect(requestActionsService.getRequestActionsByRequestId).toHaveBeenCalledTimes(1);
  });
});
