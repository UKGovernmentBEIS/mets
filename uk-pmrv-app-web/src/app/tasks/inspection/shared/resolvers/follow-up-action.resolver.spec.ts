import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';

import { of } from 'rxjs';

import { InspectionService } from '@tasks/inspection/core/inspection.service';

import { InspectionItemResolver } from './follow-up-action.resolver';

describe('InspectionItemResolver', () => {
  let resolver: InspectionItemResolver;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let inspectionService: jest.Mocked<InspectionService>;

  beforeEach(() => {
    const inspectionServiceMock = {
      payload$: of({
        installationInspection: {
          followUpActions: {
            '1': { id: '1', action: 'Action 1' },
          },
        },
      }),
    };

    TestBed.configureTestingModule({
      providers: [InspectionItemResolver, { provide: InspectionService, useValue: inspectionServiceMock }],
    });

    resolver = TestBed.inject(InspectionItemResolver);
    inspectionService = TestBed.inject(InspectionService) as jest.Mocked<InspectionService>;
  });

  it('should resolve the correct follow-up action', (done) => {
    const route = {
      paramMap: {
        get: jest.fn().mockReturnValue('1'),
      },
    } as unknown as ActivatedRouteSnapshot;

    resolver.resolve(route).subscribe((result) => {
      expect(result).toEqual({ id: '1', action: 'Action 1' });
      done();
    });
  });

  it('should return null if follow-up action is not found', (done) => {
    const route = {
      paramMap: {
        get: jest.fn().mockReturnValue('2'),
      },
    } as unknown as ActivatedRouteSnapshot;

    resolver.resolve(route).subscribe((result) => {
      expect(result).toBeNull();
      done();
    });
  });
});
