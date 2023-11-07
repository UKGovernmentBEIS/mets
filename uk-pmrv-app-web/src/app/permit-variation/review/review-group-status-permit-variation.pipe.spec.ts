import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { initialState } from '@permit-application/store/permit-application.state';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../testing';
import { PermitVariationStore } from '../store/permit-variation.store';
import { mockPermitVariationReviewOperatorLedPayload } from '../testing/mock';
import { ReviewGroupStatusPermitVariationPipe } from './review-group-status-permit-variation.pipe';

describe('ReviewGroupStatusPermitVariationPipe', () => {
  let pipe: ReviewGroupStatusPermitVariationPipe;
  let store: PermitVariationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReviewGroupStatusPermitVariationPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitVariationStore);
  });

  beforeEach(() => (pipe = new ReviewGroupStatusPermitVariationPipe(store)));

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
      ...mockPermitVariationReviewOperatorLedPayload,
      permitSectionsCompleted: {
        ...mockPermitVariationReviewOperatorLedPayload.permitSectionsCompleted,
        emissionSources: [false], //will make other sections seems as needs review
      },
      permit: {
        ...mockPermitVariationReviewOperatorLedPayload.permit,
        emissionSources: [],
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('needs review');
  });

  it('should show undecided when at least one section is in progress', async () => {
    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
      permitSectionsCompleted: {
        ...mockPermitVariationReviewOperatorLedPayload.permitSectionsCompleted,
        emissionSources: [false],
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('undecided');
  });

  it('should show accepted when every section is completed and decision is accepted', async () => {
    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
      reviewSectionsCompleted: {
        ...mockPermitVariationReviewOperatorLedPayload.reviewSectionsCompleted,
        FUELS_AND_EQUIPMENT: true,
      },
      reviewGroupDecisions: {
        ...mockPermitVariationReviewOperatorLedPayload.reviewGroupDecisions,
        FUELS_AND_EQUIPMENT: {
          type: 'ACCEPTED',
          details: {
            variationScheduleItems: [],
          },
        },
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('accepted');
  });

  it('should show rejected when every section is completed and decision is rejected', async () => {
    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
      reviewSectionsCompleted: {
        ...mockPermitVariationReviewOperatorLedPayload.reviewSectionsCompleted,
        FUELS_AND_EQUIPMENT: true,
      },
      reviewGroupDecisions: {
        ...mockPermitVariationReviewOperatorLedPayload.reviewGroupDecisions,
        FUELS_AND_EQUIPMENT: {
          type: 'REJECTED',
          details: {
            variationScheduleItems: [],
          },
        },
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('rejected');
  });

  it('should show amend needed when every section is completed and decision is amend needed', async () => {
    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
      reviewSectionsCompleted: {
        ...mockPermitVariationReviewOperatorLedPayload.reviewSectionsCompleted,
        FUELS_AND_EQUIPMENT: true,
      },
      reviewGroupDecisions: {
        ...mockPermitVariationReviewOperatorLedPayload.reviewGroupDecisions,
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
