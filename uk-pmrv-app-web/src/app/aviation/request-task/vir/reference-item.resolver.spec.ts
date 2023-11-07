import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { ReferenceItemResolver } from '@aviation/request-task/vir/reference-item.resolver';
import { ActivatedRouteSnapshotStub } from '@testing';

describe('ReferenceItemResolver', () => {
  let resolver: ReferenceItemResolver;
  let router: Router;
  let store: RequestTaskStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
    });

    store = TestBed.inject(RequestTaskStore);
    resolver = TestBed.inject(ReferenceItemResolver);
    router = TestBed.inject(Router);

    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'AVIATION_VIR_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD',
            verificationData: {
              uncorrectedNonConformities: {
                B1: {
                  reference: 'B1',
                  explanation: 'Test uncorrectedNonConformity',
                  materialEffect: true,
                },
              },
              recommendedImprovements: {
                D1: {
                  reference: 'D1',
                  explanation: 'Test recommended improvement',
                },
              },
              priorYearIssues: {
                E1: {
                  reference: 'E1',
                  explanation: 'Test priorYearIssue',
                },
              },
            },
          },
        },
      },
    } as any);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should resolve to a verification data item', async () => {
    await expect(firstValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ id: 'B1' })))).resolves.toEqual({
      explanation: 'Test uncorrectedNonConformity',
      materialEffect: true,
      reference: 'B1',
    });
  });

  it('should redirect to 404 page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValueOnce(true);

    const result = await firstValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ id: 'A4' })));
    expect(result).toEqual(undefined);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['error', '404']);
  });
});
