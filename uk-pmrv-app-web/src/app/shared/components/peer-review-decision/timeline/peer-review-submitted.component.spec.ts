import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared.module';
import { StoreContextResolver } from '../../../store-resolver/store-context.resolver';
import { PeerReviewSubmittedComponent } from './peer-review-submitted.component';

describe('PeerReviewSubmittedComponent', () => {
  let hostComponent: PeerReviewSubmittedComponent;
  let fixture: ComponentFixture<PeerReviewSubmittedComponent>;
  let store: PermitIssuanceStore;
  let location: Location;
  let page: Page;

  const storeResolver = mockClass(StoreContextResolver);
  const route = new ActivatedRouteStub({ taskId: '237' });

  class Page extends BasePage<PeerReviewSubmittedComponent> {
    get pageContents() {
      return this.queryAll<HTMLDListElement>('h1, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: StoreContextResolver, useValue: storeResolver },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState({
      ...store.getState(),
      decision: {
        type: 'AGREE',
        notes: 'I strongly agree',
      },
      requestActionCreationDate: new Date().toISOString(),
      requestActionSubmitter: 'John Bolt',
    } as any);

    location = TestBed.inject(Location);

    jest.spyOn(location, 'path').mockReturnValue('/permit-issuance/237/review/peer-reviewer-submitted');
    storeResolver.getStore.mockReturnValue(store);

    fixture = TestBed.createComponent(PeerReviewSubmittedComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should show all html elements', () => {
    expect(page.pageContents).toEqual([
      'Peer review approval',
      'Decision',
      'Agreed with the determination',
      'Supporting notes',
      'I strongly agree',
      'Peer reviewer',
      'John Bolt',
    ]);
  });
});
