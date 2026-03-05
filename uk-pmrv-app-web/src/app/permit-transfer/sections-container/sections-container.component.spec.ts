import { ChangeDetectorRef, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TaskStatusPipe } from '@permit-application/shared/pipes/task-status.pipe';
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

  const runOnPushChangeDetection = async (fixture: ComponentFixture<any>): Promise<void> => {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  };

  const setState = async (value?: any) => {
    store.setState({
      ...mockPermitTransferSubmitPayload,
      ...value,
      allowedRequestTaskActions: ['PERMIT_TRANSFER_B_SAVE_APPLICATION'],
    });

    await runOnPushChangeDetection(fixture);
  };

  @Component({
    selector: 'app-sections',
    template: `
      permit sections
    `,
  })
  class MockPermitSectionsComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SectionsContainerComponent, MockPermitSectionsComponent, TaskStatusPipe],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: TaskStatusPipe },
        {
          provide: PermitApplicationStore,
          useValue: store,
        },
      ],
    }).compileComponents();
    store = TestBed.inject(PermitTransferStore);

    fixture = TestBed.createComponent(SectionsContainerComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    requestItemsService.getItemsByRequest.mockReturnValueOnce(of({ items: [], totalItems: 0 }));
    requestActionsService.getRequestActionsByRequestId.mockReturnValueOnce(of([]));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display header, transfer details section and not display submit link', () => {
    setState();
    expect(hostElement.querySelector('app-page-heading h1').textContent).toContain('Full transfer of permit');
    expect(hostElement.querySelector('li[title="Tranfer details"] h2').textContent).toEqual('Tranfer details');
    expect(hostElement.querySelector('li[title="Tranfer details"] ul govuk-tag').textContent.trim()).toEqual(
      'not started',
    );
    expect(hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li a')).toBeNull();
    expect(
      hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li > govuk-tag').textContent.trim(),
    ).toEqual('cannot start yet');
  });

  it('should display header, transfer details section and display submit link', () => {
    setState({
      permitTransferDetailsConfirmation: {
        detailsAccepted: true,
        regulatedActivitiesInOperation: true,
        transferAccepted: true,
      },
      permitSectionsCompleted: { ...store.getState().permitSectionsCompleted, transferDetails: [true] },
    });
    expect(hostElement.querySelector('app-page-heading h1').textContent).toContain('Full transfer of permit');
    expect(hostElement.querySelector('li[title="Tranfer details"] h2').textContent).toEqual('Tranfer details');
    expect(hostElement.querySelector('li[title="Tranfer details"] ul govuk-tag').textContent.trim()).toEqual(
      'completed',
    );
    expect(hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li a')).toBeTruthy();
    expect(
      hostElement.querySelector('app-task-list > ol > li[title="Submit"] ul > li > govuk-tag').textContent.trim(),
    ).toEqual('not started');
  });
});
