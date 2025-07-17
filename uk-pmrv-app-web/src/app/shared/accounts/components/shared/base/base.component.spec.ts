import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, Observable, of } from 'rxjs';

import { AviationAccountsStore } from '@aviation/accounts/store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AccountType, AuthStore } from '@core/store';

import { RequestDetailsSearchResults, RequestSearchCriteria, RequestsService } from 'pmrv-api';

import { BaseComponent, Inspections, Reports } from './base.component';

// Mock classes for dependencies
class MockAuthStore {
  pipe() {
    return of('INSTALLATION' as AccountType);
  }
}

class MockActivatedRoute {
  data = of({ accountPermit: { account: { id: 123 } } });
}

class MockRequestsService {
  getRequestDetailsByResource() {
    return of([]);
  }
}

class MockAviationAccountsStore {
  pipe() {
    return of({ id: 456 });
  }
}

// Create a concrete class for testing
class TestBaseComponent extends BaseComponent {
  category: RequestSearchCriteria['category'] = 'INSPECTION';
  currentPageData$ = new BehaviorSubject<Reports[] | Inspections[]>([]);
  data$ = new BehaviorSubject<Reports[] | Inspections[]>([]);
}

describe('BaseComponent', () => {
  let component: BaseComponent;
  let fixture: ComponentFixture<BaseComponent>;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let authStore: AuthStore;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let route: ActivatedRoute;
  let requestsService: RequestsService;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let store: AviationAccountsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BaseComponent],
      providers: [
        UntypedFormBuilder,
        DestroySubject,
        { provide: AuthStore, useClass: MockAuthStore },
        { provide: ActivatedRoute, useClass: MockActivatedRoute },
        { provide: RequestsService, useClass: MockRequestsService },
        { provide: AviationAccountsStore, useClass: MockAviationAccountsStore },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestBaseComponent);
    component = fixture.componentInstance;
    authStore = TestBed.inject(AuthStore);
    route = TestBed.inject(ActivatedRoute);
    requestsService = TestBed.inject(RequestsService);
    store = TestBed.inject(AviationAccountsStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with correct controls', () => {
    expect(component.searchForm.contains('types')).toBe(true);
    expect(component.searchForm.contains('statuses')).toBe(true);
  });

  it('should set domain based on currentDomain$', () => {
    component.ngOnInit();
    component.currentDomain$.subscribe((domain) => {
      expect(domain).toBe('INSTALLATION');
    });
  });

  it('should set accountId$ based on domain', () => {
    component.ngOnInit();
    component.accountId$.subscribe((accountId) => {
      expect(accountId).toBe(123);
    });
  });

  it('should update search types on form value changes', () => {
    component.ngOnInit();
    const mockTypes = ['TYPE1', 'TYPE2'];
    component.searchForm.get('types').setValue(mockTypes);
    component.searchTypes$.subscribe((types) => {
      expect(types).toEqual(mockTypes);
    });
  });

  it('should update search statuses on form value changes', () => {
    component.ngOnInit();
    const mockStatuses = ['STATUS1', 'STATUS2'];
    component.searchForm.get('statuses').setValue(mockStatuses);
    component.searchStatuses$.subscribe((statuses) => {
      expect(statuses).toEqual(mockStatuses);
    });
  });

  it('should call requestsService.getRequestDetailsByResource in getRequestDetails', () => {
    jest
      .spyOn(requestsService, 'getRequestDetailsByResource')
      .mockImplementation(() => of([]) as Observable<RequestDetailsSearchResults>);
    component.getRequestDetails().subscribe();
    expect(requestsService.getRequestDetailsByResource).toHaveBeenCalled();
  });

  it('should group details by year correctly', () => {
    const mockData = [
      { requestMetadata: { year: 2021 }, id: 1 },
      { requestMetadata: { year: 2022 }, id: 2 },
      { requestMetadata: { year: 2021 }, id: 3 },
    ] as any;
    const groupedData = component.groupDetailsByYear(mockData);
    expect(groupedData[2021].length).toBe(2);
    expect(groupedData[2022].length).toBe(1);
  });
});
