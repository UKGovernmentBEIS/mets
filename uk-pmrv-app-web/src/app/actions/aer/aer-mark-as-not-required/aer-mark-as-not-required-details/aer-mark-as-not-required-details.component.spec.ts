import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { mockClass } from '@testing';

import { RequestActionsService } from 'pmrv-api';

import { AerMarkAsNotRequiredDetailsComponent } from './aer-mark-as-not-required-details.component';

describe('AerMarkAsNotRequiredDetailsComponent', () => {
  let component: AerMarkAsNotRequiredDetailsComponent;
  let fixture: ComponentFixture<AerMarkAsNotRequiredDetailsComponent>;
  let mockedRequestActionsService: jest.Mocked<RequestActionsService>;
  let mockedBackLinkService: jest.Mocked<BackLinkService>;

  beforeEach(waitForAsync(() => {
    mockedRequestActionsService = mockClass(RequestActionsService);
    mockedBackLinkService = mockClass(BackLinkService);

    TestBed.configureTestingModule({
      declarations: [AerMarkAsNotRequiredDetailsComponent],
      imports: [HttpClientTestingModule],
      providers: [
        { provide: RequestActionsService, useValue: mockedRequestActionsService },
        { provide: BackLinkService, useValue: mockedBackLinkService },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({
              get: () => '123',
            }),
          },
        },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AerMarkAsNotRequiredDetailsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call BackLinkService.show() on init', () => {
    component.ngOnInit();
    expect(mockedBackLinkService.show).toHaveBeenCalled();
  });

  it('should load action$ with requestAction data', () => {
    const mockAction = {
      id: 123,
      payload: {
        markNotRequiredDetails: {
          reason: 'Some reason',
        },
      },
      creationDate: '2024-10-10T00:00:00Z',
    };

    mockedRequestActionsService.getRequestActionById.mockReturnValueOnce(of(mockAction));

    component.action$.subscribe((action) => {
      expect(action).toEqual({
        id: 123,
        payload: mockAction.payload,
        creationDate: '2024-10-10T00:00:00Z',
      });
    });
    expect(mockedRequestActionsService.getRequestActionById).toHaveBeenCalledWith(123);
  });
});
