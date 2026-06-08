import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { TaskStatusPipe } from '@permit-application/shared/pipes/task-status.pipe';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { RequestActionsService, RequestItemsService } from 'pmrv-api';

import { PermitTransferStore } from '../store/permit-transfer.store';
import { mockPermitTransferSubmitPayload } from '../testing/mock';
import { SectionsContainerComponent } from './sections-container.component';

describe('TransferSectionsContainerComponent', () => {
  let component: SectionsContainerComponent;
  let fixture: ComponentFixture<SectionsContainerComponent>;
  let hostElement: HTMLElement;
  let store: PermitTransferStore;

  const requestItemsService = mockClass(RequestItemsService);
  const requestActionsService = mockClass(RequestActionsService);

  const createComponent = async (value?: any) => {
    store = TestBed.inject(PermitTransferStore);
    store.setState({
      ...mockPermitTransferSubmitPayload,
      ...value,
      allowedRequestTaskActions: ['PERMIT_TRANSFER_B_SAVE_APPLICATION'],
    });

    fixture = TestBed.createComponent(SectionsContainerComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    requestItemsService.getItemsByRequest.mockReturnValueOnce(of({ items: [], totalItems: 0 }));
    requestActionsService.getRequestActionsByRequestId.mockReturnValueOnce(of([]));
    fixture.detectChanges();
  };

  @Component({
    selector: 'app-test-sections',
    standalone: false,
    template: `
      permit sections
    `,
  })
  class MockPermitSectionsComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SectionsContainerComponent, MockPermitSectionsComponent, TaskStatusPipe],
      imports: [SharedModule, SharedPermitModule],
      providers: [
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: TaskStatusPipe },
        {
          provide: PermitApplicationStore,
          useExisting: PermitTransferStore,
        },
        provideRouter([]),
      ],
    }).compileComponents();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should display header, transfer details section and not display submit link', () => {
    createComponent();

    expect(hostElement.querySelector('app-page-heading h1').textContent).toContain('Full transfer of permit');
    expect(hostElement.querySelector('li[title="Transfer details"] h2').textContent).toEqual('Transfer details');
    expect(hostElement.querySelector('li[title="Transfer details"] ul govuk-tag').textContent.trim()).toEqual(
      'not started',
    );
    expect(hostElement.querySelector('ol > li:last-child ul > li a')).toBeNull();
    expect(hostElement.querySelector('ol > li:last-child ul > li > govuk-tag').textContent.trim()).toEqual(
      'cannot start yet',
    );
  });

  it('should display header, transfer details section and display submit link', () => {
    createComponent({
      permitTransferDetailsConfirmation: {
        detailsAccepted: true,
        regulatedActivitiesInOperation: true,
        transferAccepted: true,
      },
      permitSectionsCompleted: { ...store.getState().permitSectionsCompleted, transferDetails: [true] },
    });

    expect(hostElement.querySelector('app-page-heading h1').textContent).toContain('Full transfer of permit');
    expect(hostElement.querySelector('li[title="Transfer details"] h2').textContent).toEqual('Transfer details');
    expect(hostElement.querySelector('li[title="Transfer details"] ul govuk-tag').textContent.trim()).toEqual(
      'completed',
    );
    expect(hostElement.querySelector('ol > li:last-child ul > li a')).toBeTruthy();
    expect(hostElement.querySelector('ol > li:last-child ul > li > govuk-tag').textContent.trim()).toEqual(
      'not started',
    );
  });
});
