import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub } from '@testing';
import { screen } from '@testing-library/angular';

import { ReturnForAmendsPageComponent } from './return-for-amends-page.component';

describe('ReturnForAmendsPageComponent', () => {
  let component: ReturnForAmendsPageComponent;
  let fixture: ComponentFixture<ReturnForAmendsPageComponent>;
  let location: Location;

  const route: ActivatedRouteStub = new ActivatedRouteStub({ taskId: '123' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();
    location = TestBed.inject(Location);

    jest.spyOn(location, 'path').mockReturnValue('/aviation/tasks/123/aer/review/return-for-amends');

    const aviationStore = TestBed.inject(RequestTaskStore);

    const state = aviationStore.getState();

    aviationStore.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        allowedRequestTaskActions: ['AVIATION_AER_UKETS_REVIEW_RETURN_FOR_AMENDS'],
        requestTask: {
          type: 'AVIATION_AER_UKETS_APPLICATION_REVIEW',
          payload: {
            ...EmpUkEtsStoreDelegate.INITIAL_STATE,
            reviewAttachments: { '52d7a1e1-88ab-4fc9-9494-43739e5ba8d3': 'test.png' },
            reviewGroupDecisions: {
              EMISSIONS_REDUCTION_CLAIM: {
                type: 'OPERATOR_AMENDS_NEEDED',
                notes: 'Notes',
                changesRequired: 'Changes',
              },
            },
          } as unknown as AerRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(ReturnForAmendsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show correct page header, section header and changes required', () => {
    expect(screen.getByText(/Check your information before sending/)).toBeTruthy();
    expect(screen.getByText(/Emissions reduction claim/)).toBeTruthy();
    expect(screen.getByText(/Changes required/)).toBeTruthy();
  });
});
