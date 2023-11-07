import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { initialState } from '../../store/common-tasks.state';
import { CommonTasksStore } from '../../store/common-tasks.store';
import { updateMockDoalApplicationSubmitRequestTaskItem } from '../test/mock';
import { SummaryGuard } from './summary.guard';

describe('SummaryGuard', () => {
  let guard: SummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  describe('For operator activity level report', () => {
    beforeEach(() => {
      activatedRouteSnapshot = new ActivatedRouteSnapshot();
      activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
      activatedRouteSnapshot.params = { taskId: 1 };
      activatedRouteSnapshot.data = { sectionKey: 'operatorActivityLevelReport' };
    });

    const routerStateSnapshot = {
      url: '/tasks/1/doal/submit/operator-report/summary',
    } as RouterStateSnapshot;

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should return true if section completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...updateMockDoalApplicationSubmitRequestTaskItem({}, { operatorActivityLevelReport: true }),
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should return true if wizard completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            operatorActivityLevelReport: {
              document: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
              areActivityLevelsEstimated: false,
              comment: 'operatorActivityLevelReport',
            },
          },
          { operatorActivityLevelReport: false },
        ),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to base url if section not completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({ operatorActivityLevelReport: undefined }, {}),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/operator-report`));
    });
  });

  describe('For verification activity level report', () => {
    beforeEach(() => {
      activatedRouteSnapshot = new ActivatedRouteSnapshot();
      activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
      activatedRouteSnapshot.params = { taskId: 1 };
      activatedRouteSnapshot.data = { sectionKey: 'verificationReportOfTheActivityLevelReport' };
    });

    const routerStateSnapshot = {
      url: '/tasks/1/doal/submit/verification-report/summary',
    } as RouterStateSnapshot;

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should return true if section completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...updateMockDoalApplicationSubmitRequestTaskItem({}, { verificationReportOfTheActivityLevelReport: true }),
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should return true if wizard completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            verificationReportOfTheActivityLevelReport: {
              document: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
              comment: 'operatorActivityLevelReport',
            },
          },
          { verificationReportOfTheActivityLevelReport: false },
        ),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to base url if section not completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          { verificationReportOfTheActivityLevelReport: undefined },
          {},
        ),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/verification-report`));
    });
  });

  describe('For additional documents', () => {
    beforeEach(() => {
      activatedRouteSnapshot = new ActivatedRouteSnapshot();
      activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
      activatedRouteSnapshot.params = { taskId: 1 };
      activatedRouteSnapshot.data = { sectionKey: 'additionalDocuments' };
    });

    const routerStateSnapshot = {
      url: '/tasks/1/doal/submit/additional-documents/summary',
    } as RouterStateSnapshot;

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should return true if section completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...updateMockDoalApplicationSubmitRequestTaskItem({}, { additionalDocuments: true }),
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should return true if wizard completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            additionalDocuments: {
              exist: true,
              documents: ['7e2036b4-c857-4caa-afef-97e690df3454'],
              comment: 'comm',
            },
          },
          { additionalDocuments: false },
        ),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to base url if section not completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({ additionalDocuments: undefined }, {}),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/additional-documents`));
    });
  });

  describe('For activity Level Change Information', () => {
    beforeEach(() => {
      activatedRouteSnapshot = new ActivatedRouteSnapshot();
      activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
      activatedRouteSnapshot.params = { taskId: 1 };
      activatedRouteSnapshot.data = { sectionKey: 'activityLevelChangeInformation' };
    });

    const routerStateSnapshot = {
      url: '/tasks/1/doal/submit/alc-information/summary',
    } as RouterStateSnapshot;

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should return true if section completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...updateMockDoalApplicationSubmitRequestTaskItem({}, { activityLevelChangeInformation: true }),
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should return true if enableViewSummary flag exists', async () => {
      jest
        .spyOn(router, 'getCurrentNavigation')
        .mockReturnValue({ extras: { state: { enableViewSummary: true } } } as any);
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...updateMockDoalApplicationSubmitRequestTaskItem({}, { activityLevelChangeInformation: false }),
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to base url if enableViewSummary flag not exist', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({}, { activityLevelChangeInformation: false }),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/alc-information`));
    });
  });

  describe('For determination closed', () => {
    beforeEach(() => {
      activatedRouteSnapshot = new ActivatedRouteSnapshot();
      activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
      activatedRouteSnapshot.params = { taskId: 1 };
      activatedRouteSnapshot.data = { sectionKey: 'determination' };
    });

    const routerStateSnapshot = {
      url: '/tasks/1/doal/submit/determination/close/summary',
    } as RouterStateSnapshot;

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should return true if section completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...updateMockDoalApplicationSubmitRequestTaskItem(
            {
              determination: {
                type: 'CLOSED',
                reason: 'close reason',
              },
            },
            { determination: true },
          ),
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should return true if wizard completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            determination: {
              type: 'CLOSED',
              reason: 'close reason',
            },
          },
          { determination: false },
        ),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to base determination url if section not completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            determination: {
              type: 'CLOSED',
            } as any,
          },
          { determination: false },
        ),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/determination`));
    });
  });

  describe('For determination proceed to authority', () => {
    beforeEach(() => {
      activatedRouteSnapshot = new ActivatedRouteSnapshot();
      activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
      activatedRouteSnapshot.params = { taskId: 1 };
      activatedRouteSnapshot.data = { sectionKey: 'determination' };
    });

    const routerStateSnapshot = {
      url: '/tasks/1/doal/submit/determination/proceed-authority/summary',
    } as RouterStateSnapshot;

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should return true if section completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...updateMockDoalApplicationSubmitRequestTaskItem({}, { determination: true }),
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should return true if wizard completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({}, { determination: false }),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to base determination url if section not completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
          {
            determination: {
              type: 'PROCEED_TO_AUTHORITY',
              reason: 'reason',
            } as any,
          },
          { determination: false },
        ),
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/determination`));
    });
  });
});
