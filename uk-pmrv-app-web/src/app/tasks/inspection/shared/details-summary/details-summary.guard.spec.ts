import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { of } from 'rxjs';

import { InspectionService } from '@tasks/inspection/core/inspection.service';

import { isInstallationInspectionDetailsSubmitCompleted } from '../../submit/submit.wizard';
import { detailsSummaryGuard } from './details-summary.guard';

jest.mock('@angular/core', () => ({
  ...jest.requireActual('@angular/core'),
  inject: jest.fn((token) => TestBed.inject(token)),
}));

jest.mock('../../submit/submit.wizard', () => ({
  isInstallationInspectionDetailsSubmitCompleted: jest.fn(),
}));

describe('detailsSummaryGuard', () => {
  let router: jest.Mocked<Router>;
  let inspectionService: jest.Mocked<InspectionService>;
  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    router = {
      parseUrl: jest.fn(),
    } as any;

    inspectionService = {
      payload$: of({}),
    } as any;

    activatedRouteSnapshot = {
      paramMap: new Map([
        ['taskId', '123'],
        ['type', 'someType'],
      ]),
    } as any;

    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: router },
        { provide: InspectionService, useValue: inspectionService },
      ],
    });
  });

  it('should return true when isInstallationInspectionDetailsSubmitCompleted returns true', (done) => {
    (isInstallationInspectionDetailsSubmitCompleted as jest.Mock).mockReturnValue(true);

    detailsSummaryGuard(activatedRouteSnapshot).subscribe((result) => {
      expect(result).toBe(true);
      done();
    });
  });

  it('should return UrlTree when isInstallationInspectionDetailsSubmitCompleted returns false', (done) => {
    const mockUrlTree = {} as UrlTree;
    (isInstallationInspectionDetailsSubmitCompleted as jest.Mock).mockReturnValue(false);
    router.parseUrl.mockReturnValue(mockUrlTree);

    detailsSummaryGuard(activatedRouteSnapshot).subscribe((result) => {
      expect(result).toBe(mockUrlTree);
      expect(router.parseUrl).toHaveBeenCalledWith('tasks/123/inspection/someType/submit/details');
      done();
    });
  });
});
