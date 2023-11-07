import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../testing';
import { PermitVariationStore } from '../store/permit-variation.store';
import { mockPermitVariationRegulatorLedPayload } from '../testing/mock';
import { ReviewGroupStatusPermitVariationPipe } from './review-group-status-permit-variation.pipe';
import { ReviewGroupStatusPermitVariationRegulatorLedPipe } from './review-group-status-permit-variation-regulator-led.pipe';

describe('ReviewGroupStatusPermitVariationRegulatorLedPipe', () => {
  let pipe: ReviewGroupStatusPermitVariationRegulatorLedPipe;
  let store: PermitVariationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReviewGroupStatusPermitVariationPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitVariationStore);
  });

  beforeEach(() => (pipe = new ReviewGroupStatusPermitVariationRegulatorLedPipe(store)));

  it('should show needs review when at least one section is in needs review', async () => {
    store.setState({
      ...mockPermitVariationRegulatorLedPayload,
      permitSectionsCompleted: {
        ...mockPermitVariationRegulatorLedPayload.permitSectionsCompleted,
        emissionSources: [false], //will make other sections seems as needs review
      },
      permit: {
        ...mockPermitVariationRegulatorLedPayload.permit,
        emissionSources: [],
      },
    });
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('needs review');
  });

  it('should show in progress when at least one section is in progress', async () => {
    store.setState({
      ...mockPermitVariationRegulatorLedPayload,
      permitSectionsCompleted: {
        ...mockPermitVariationRegulatorLedPayload.permitSectionsCompleted,
        TRANSFERRED_CO2_N2O_Transport_Network_Approach: [false], //will make other sections seems as needs review
      },
      permit: {
        ...mockPermitVariationRegulatorLedPayload.permit,
        monitoringApproaches: {
          ...mockPermitVariationRegulatorLedPayload.permit.monitoringApproaches,
          TRANSFERRED_CO2_N2O: {
            monitoringTransportNetworkApproach: 'METHOD_A',
          } as any,
        },
      },
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O'))).resolves.toEqual('in progress');
  });

  it('should show complete when all sections are complete', async () => {
    store.setState({
      ...mockPermitVariationRegulatorLedPayload,
    });
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2_N2O'))).resolves.toEqual('complete');
  });
});
