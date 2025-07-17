import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { SharedModule } from '@shared/shared.module';
import { StoreContextResolver } from '@shared/store-resolver/store-context.resolver';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { DoePeerReviewComponent } from './doe-peer-review.component';

describe('PeerReviewComponent for DOE', () => {
  let component: DoePeerReviewComponent;
  let fixture: ComponentFixture<DoePeerReviewComponent>;
  let location: Location;

  const storeResolver = mockClass(StoreContextResolver);
  const route: ActivatedRouteStub = new ActivatedRouteStub({ taskId: '237' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: route },
        { provide: StoreContextResolver, useValue: storeResolver },
      ],
    }).compileComponents();
    location = TestBed.inject(Location);

    storeResolver.getRequestTaskType.mockReturnValue(of('AVIATION_DOE_CORSIA_APPLICATION_SUBMIT'));
    storeResolver.getRequestId.mockReturnValue(of('1'));

    jest.spyOn(location, 'path').mockReturnValue('/aviation/tasks/237/doe/review/peer-review');

    const aviationStore = TestBed.inject(RequestTaskStore);

    const state = aviationStore.getState();

    aviationStore.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        allowedRequestTaskActions: ['AVIATION_DOE_CORSIA_REQUEST_PEER_REVIEW'],
        requestTask: {
          type: 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT',
        },
      },
    });

    fixture = TestBed.createComponent(DoePeerReviewComponent);
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
