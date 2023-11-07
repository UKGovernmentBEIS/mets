import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { SharedModule } from '@shared/shared.module';
import { StoreContextResolver } from '@shared/store-resolver/store-context.resolver';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { EmpPeerReviewComponent } from './peer-review.component';

describe('PeerReviewComponent', () => {
  let component: EmpPeerReviewComponent;
  let fixture: ComponentFixture<EmpPeerReviewComponent>;
  let location: Location;

  const storeResolver = mockClass(StoreContextResolver);
  const route: ActivatedRouteStub = new ActivatedRouteStub({ taskId: '237' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: StoreContextResolver, useValue: storeResolver },
      ],
    }).compileComponents();
    location = TestBed.inject(Location);

    storeResolver.getRequestTaskType.mockReturnValue(of('EMP_ISSUANCE_UKETS_APPLICATION_REVIEW'));
    storeResolver.getRequestId.mockReturnValue(of('1'));

    jest.spyOn(location, 'path').mockReturnValue('/aviation/tasks/237/emp/review/peer-review');

    const aviationStore = TestBed.inject(RequestTaskStore);

    const state = aviationStore.getState();

    aviationStore.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        allowedRequestTaskActions: ['EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW'],
        requestTask: {
          type: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
          payload: {
            ...EmpUkEtsStoreDelegate.INITIAL_STATE,
            reviewSectionsCompleted: {},
          } as EmpRequestTaskPayloadUkEts,
        },
      },
    });

    fixture = TestBed.createComponent(EmpPeerReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show header', () => {
    expect(screen.getByText(/Send for peer review/)).toBeTruthy();
  });
});
