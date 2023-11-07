import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { initialState } from '@permit-application/store/permit-application.state';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../testing';
import { mockPermitCompletePayload } from '../../permit-application/testing/mock-permit-apply-action';
import { mockReviewState } from '../../permit-application/testing/mock-state';
import { PermitIssuanceStore } from '../store/permit-issuance.store';
import { ReviewGroupStatusPermitIssuancePipe } from './review-group-status-permit-issuance.pipe';

describe('ReviewGroupStatusPermitIssuancePipe', () => {
  let pipe: ReviewGroupStatusPermitIssuancePipe;
  let store: PermitIssuanceStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReviewGroupStatusPermitIssuancePipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitIssuanceStore);
  });

  beforeEach(() => (pipe = new ReviewGroupStatusPermitIssuancePipe(store)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should show request action as accepted', async () => {
    store.setState({
      ...initialState,
      isRequestTask: false,
    });
    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual('accepted');
  });

  it('should show needs review when at least one section is in needs review', async () => {
    store.setState({
      ...mockReviewState,
      ...mockPermitCompletePayload,
      permitSectionsCompleted: {
        ...mockPermitCompletePayload.permitSectionsCompleted,
        emissionSources: [false], //will make other sections seems as needs review
      },
      permit: {
        ...mockPermitCompletePayload.permit,
        emissionSources: [],
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('needs review');
  });

  it('should show undecided when at least one section is in progress', async () => {
    store.setState({
      ...mockReviewState,
      ...mockPermitCompletePayload,
      permitSectionsCompleted: {
        ...mockPermitCompletePayload.permitSectionsCompleted,
        emissionSources: [false],
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('undecided');
  });

  it('should show accepted when every section is completed and decision is accepted', async () => {
    store.setState({
      ...mockReviewState,
      ...mockPermitCompletePayload,
      reviewSectionsCompleted: {
        ...mockReviewState.reviewSectionsCompleted,
        FUELS_AND_EQUIPMENT: true,
      },
      reviewGroupDecisions: {
        ...mockReviewState.reviewGroupDecisions,
        FUELS_AND_EQUIPMENT: {
          type: 'ACCEPTED',
          details: {},
        },
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('accepted');
  });

  it('should show rejected when every section is completed and decision is rejected', async () => {
    store.setState({
      ...mockReviewState,
      ...mockPermitCompletePayload,
      reviewSectionsCompleted: {
        ...mockReviewState.reviewSectionsCompleted,
        FUELS_AND_EQUIPMENT: true,
      },
      reviewGroupDecisions: {
        ...mockReviewState.reviewGroupDecisions,
        FUELS_AND_EQUIPMENT: {
          type: 'REJECTED',
          details: {},
        },
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('rejected');
  });

  it('should show amend needed when every section is completed and decision is amend needed', async () => {
    store.setState({
      ...mockReviewState,
      ...mockPermitCompletePayload,
      reviewSectionsCompleted: {
        ...mockReviewState.reviewSectionsCompleted,
        FUELS_AND_EQUIPMENT: true,
      },
      reviewGroupDecisions: {
        ...mockReviewState.reviewGroupDecisions,
        FUELS_AND_EQUIPMENT: {
          type: 'OPERATOR_AMENDS_NEEDED',
          details: {
            requiredChanges: [{ reason: 'reason' }],
          },
        },
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('operator to amend');
  });
});
