import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import { AerMarkAsNotRequiredComponent } from './aer-mark-as-not-required.component';

describe('AerMarkAsNotRequiredComponent', () => {
  let component: AerMarkAsNotRequiredComponent;
  let fixture: ComponentFixture<AerMarkAsNotRequiredComponent>;
  let mockRequestsService: jest.Mocked<RequestsService>;
  let mockActivatedRoute: Partial<ActivatedRoute>;

  beforeEach(async () => {
    mockRequestsService = mockClass(RequestsService);

    mockActivatedRoute = {
      paramMap: of({
        get: (key: string) => {
          const params = { accountId: '123', 'request-id': 'abc123' };
          return params[key];
        },
      }),
    };

    await TestBed.configureTestingModule({
      declarations: [AerMarkAsNotRequiredComponent],
      providers: [
        { provide: PendingRequestService, useValue: new PendingRequestService() },
        { provide: RequestsService, useValue: mockRequestsService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AerMarkAsNotRequiredComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize accountId$ with route param accountId', () => {
    component.accountId$.subscribe((accountId) => {
      expect(accountId).toBe(123);
    });
  });

  it('should initialize requestId$ with route param request-id', () => {
    component.requestId$.subscribe((requestId) => {
      expect(requestId).toBe('abc123');
    });
  });

  it('should not call requestsService.markAsNotRequired with the request-id on confirm', () => {
    mockRequestsService.markAsNotRequired.mockReturnValueOnce(of(null));
    component.onConfirmMarkAsNotRequired();
    expect(mockRequestsService.markAsNotRequired).toHaveBeenCalledTimes(0);
  });

  it('should call requestsService.markAsNotRequired with the request-id on confirm', () => {
    mockRequestsService.markAsNotRequired.mockReturnValueOnce(of(null));
    component.form.get('reason').setValue('Test');
    component.onConfirmMarkAsNotRequired();
    expect(mockRequestsService.markAsNotRequired).toHaveBeenCalledTimes(1);
    expect(mockRequestsService.markAsNotRequired).toHaveBeenCalledWith('abc123', { reason: 'Test' });
  });

  it('should update isMarkedAsNotRequired$ to true after markAsNotRequired succeeds', () => {
    mockRequestsService.markAsNotRequired.mockReturnValueOnce(of(null));

    component.onConfirmMarkAsNotRequired();

    component.isMarkedAsNotRequired$.subscribe((isMarkedAsNotRequired) => {
      expect(isMarkedAsNotRequired).toBeTrue();
    });
  });

  it('should update reference$ with the request-id on confirm', () => {
    mockRequestsService.markAsNotRequired.mockReturnValueOnce(of(null));

    component.onConfirmMarkAsNotRequired();

    component.reference$.subscribe((reference) => {
      expect(reference).toBe('abc123');
    });
  });
});
