import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { PermitIssuanceReviewDecision, PermitVariationReviewDecision, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../../permit-application/store/permit-application.store';
import { PermitVariationStore } from '../../../store/permit-variation.store';
import { ReviewGroupVariationPipe } from './review-group-variation.pipe';

describe('ReviewGroupVariationPipe', () => {
  let pipe: ReviewGroupVariationPipe;
  let store: PermitVariationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReviewGroupVariationPipe],
      providers: [
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitVariationStore,
        },
      ],
    });

    store = TestBed.inject(PermitVariationStore);
  });

  beforeEach(() => (pipe = new ReviewGroupVariationPipe(store)));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return an observable of the review group decision', async () => {
    const installationDetails = { type: 'ACCEPTED', notes: 'notes' } as PermitIssuanceReviewDecision;

    store.setState({ ...store.getState(), reviewGroupDecisions: { INSTALLATION_DETAILS: installationDetails } });

    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual(installationDetails);
  });

  it('should return null for not existence', async () => {
    store.setState({ ...store.getState(), reviewGroupDecisions: {} });

    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual(null);
  });

  it('should return about variation review group', async () => {
    const aboutVariationDecision = {
      type: 'ACCEPTED',
      details: {
        notes: 'notes',
        variationScheduleItems: ['gg'],
      },
    } as PermitVariationReviewDecision;
    store.setState({ ...store.getState(), permitVariationDetailsReviewDecision: aboutVariationDecision });

    await expect(firstValueFrom(pipe.transform('ABOUT_VARIATION'))).resolves.toEqual(aboutVariationDecision);
  });
});
