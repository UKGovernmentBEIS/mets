import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { mockState } from '@permit-application/testing/mock-state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { mockClass } from '../../../testing';
import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { mockTaskState as revocationMockTaskState } from '../../permit-revocation/testing/mock-state';
import { PermitSurrenderStore } from '../../permit-surrender/store/permit-surrender.store';
import { mockTaskState as surrenderMockTaskState } from '../../permit-surrender/testing/mock-state';
import { PermitVariationStore } from '../../permit-variation/store/permit-variation.store';
import { initialState as rdeInitialState } from '../../rde/store/rde.state';
import { RdeStore } from '../../rde/store/rde.store';
import { initialState as rfiInitialState } from '../../rfi/store/rfi.state';
import { RfiStore } from '../../rfi/store/rfi.store';
import { StoreContextResolver } from '../store-resolver/store-context.resolver';

describe('PaymentCompletedGuard', () => {
  let guard: PaymentCompletedGuard;
  let router: Router;
  const storeResolver = mockClass(StoreContextResolver);

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 123 };

  describe('issuance', () => {
    const routerState = { url: '/permit-issuance/15/review' } as RouterStateSnapshot;
    let store: PermitIssuanceStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [PaymentCompletedGuard, { provide: StoreContextResolver, useValue: storeResolver }],
      });
      guard = TestBed.inject(PaymentCompletedGuard);
      router = TestBed.inject(Router);

      store = TestBed.inject(PermitIssuanceStore);
      storeResolver.getStore.mockReturnValue(store);
    });

    it('for permit issuance should be true if paymentCompleted is true', async () => {
      store.setState({ ...mockState, paymentCompleted: true });
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(true);
    });

    it('should redirect to payment not completed page if paymentCompleted is false', async () => {
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(
        router.parseUrl(`/permit-issuance/${activatedRouteSnapshot.params.taskId}/review/payment-not-completed`),
      );
    });
  });

  describe('variation', () => {
    const routerState = { url: '/permit-variation/15/review' } as RouterStateSnapshot;
    let store: PermitVariationStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [PaymentCompletedGuard, { provide: StoreContextResolver, useValue: storeResolver }],
      });
      guard = TestBed.inject(PaymentCompletedGuard);
      router = TestBed.inject(Router);

      store = TestBed.inject(PermitVariationStore);
      storeResolver.getStore.mockReturnValue(store);
    });

    it('for permit variation should be true if payment is not required', async () => {
      store.setState({
        ...mockState,
        requestTaskType: 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT',
        paymentCompleted: false,
      });
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(true);
    });
  });

  describe('surrender', () => {
    const routerState = { url: '/permit-surrender/15/review' } as RouterStateSnapshot;
    let store: PermitSurrenderStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [PaymentCompletedGuard, { provide: StoreContextResolver, useValue: storeResolver }],
      });
      guard = TestBed.inject(PaymentCompletedGuard);
      router = TestBed.inject(Router);

      store = TestBed.inject(PermitSurrenderStore);
      storeResolver.getStore.mockReturnValue(store);
    });

    it('should be true if paymentCompleted is true', async () => {
      store.setState({ ...surrenderMockTaskState, paymentCompleted: true });
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(true);
    });

    it('should redirect to payment not completed page if paymentCompleted is false', async () => {
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(
        router.parseUrl(`/permit-surrender/${activatedRouteSnapshot.params.taskId}/review/payment-not-completed`),
      );
    });
  });

  describe('revocation', () => {
    const routerState = { url: '/permit-revocation/15' } as RouterStateSnapshot;
    let store: PermitRevocationStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [PaymentCompletedGuard, { provide: StoreContextResolver, useValue: storeResolver }],
      });
      guard = TestBed.inject(PaymentCompletedGuard);
      router = TestBed.inject(Router);

      store = TestBed.inject(PermitRevocationStore);
      storeResolver.getStore.mockReturnValue(store);
    });

    it('should be true if paymentCompleted is true', async () => {
      store.setState({ ...revocationMockTaskState, paymentCompleted: true });
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(true);
    });

    it('should redirect to payment not completed page if paymentCompleted is false', async () => {
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(
        router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/payment-not-completed`),
      );
    });
  });

  describe('rfi', () => {
    const routerState = { url: '/rfi/15' } as RouterStateSnapshot;
    let store: RfiStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [PaymentCompletedGuard, { provide: StoreContextResolver, useValue: storeResolver }],
      });
      guard = TestBed.inject(PaymentCompletedGuard);
      router = TestBed.inject(Router);

      store = TestBed.inject(RfiStore);
      storeResolver.getStore.mockReturnValue(store);
    });

    it('should be true if paymentCompleted is true', async () => {
      store.setState({ ...rfiInitialState, paymentCompleted: true });
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(true);
    });

    it('should redirect to payment not completed page if paymentCompleted is false', async () => {
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(
        router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/payment-not-completed`),
      );
    });
  });

  describe('rde', () => {
    const routerState = { url: '/rde/15' } as RouterStateSnapshot;
    let store: RdeStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [PaymentCompletedGuard, { provide: StoreContextResolver, useValue: storeResolver }],
      });
      guard = TestBed.inject(PaymentCompletedGuard);
      router = TestBed.inject(Router);

      store = TestBed.inject(RdeStore);
      storeResolver.getStore.mockReturnValue(store);
    });

    it('should be true if paymentCompleted is true', async () => {
      store.setState({ ...rdeInitialState, paymentCompleted: true });
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(true);
    });

    it('should redirect to payment not completed page if paymentCompleted is false', async () => {
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(
        router.parseUrl(`/rde/${activatedRouteSnapshot.params.taskId}/payment-not-completed`),
      );
    });
  });

  describe('EMP', () => {
    const routerState = {
      url: `/aviation/tasks/${activatedRouteSnapshot.params.taskId}`,
    } as RouterStateSnapshot;
    let store: RequestTaskStore;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule],
        providers: [PaymentCompletedGuard, { provide: StoreContextResolver, useValue: storeResolver }],
      });
      guard = TestBed.inject(PaymentCompletedGuard);
      router = TestBed.inject(Router);

      store = TestBed.inject(RequestTaskStore);
      storeResolver.getStore.mockReturnValue(store);
    });

    it('should be true if paymentCompleted is true', async () => {
      store.setState({
        requestTaskItem: {
          requestInfo: { paymentCompleted: true },
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
          },
        },
        relatedTasks: [],
        timeline: [],
        isTaskReassigned: false,
        taskReassignedTo: '',
        isEditable: true,
        tasksState: {},
      });
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(true);
    });

    it('should redirect to payment not completed page if paymentCompleted is false', async () => {
      store.setState({
        requestTaskItem: {
          requestInfo: { paymentCompleted: false },
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
          },
        },
        relatedTasks: [],
        timeline: [],
        isTaskReassigned: false,
        taskReassignedTo: '',
        isEditable: true,
        tasksState: {},
      });
      await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerState))).resolves.toEqual(
        router.parseUrl(`/aviation/tasks/${activatedRouteSnapshot.params.taskId}/payment-not-completed`),
      );
    });
  });
});
