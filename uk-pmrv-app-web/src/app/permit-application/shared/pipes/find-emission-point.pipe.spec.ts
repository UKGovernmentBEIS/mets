import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { FindEmissionPointPipe } from './find-emission-point.pipe';

describe('FindEmissionPointPipe', () => {
  let pipe: FindEmissionPointPipe;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [FindEmissionPointPipe],
      providers: [
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new FindEmissionPointPipe(store)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should find emission point', async () => {
    store.setState(mockState);
    await expect(firstValueFrom(pipe.transform(mockPermitApplyPayload.permit.emissionPoints[0].id))).resolves.toEqual(
      mockPermitApplyPayload.permit.emissionPoints[0],
    );
  });

  it('should not find emission point', async () => {
    store.setState(mockState);
    await expect(firstValueFrom(pipe.transform('test'))).resolves.toBeFalsy();
    await expect(firstValueFrom(pipe.transform())).resolves.toBeFalsy();
  });
});
