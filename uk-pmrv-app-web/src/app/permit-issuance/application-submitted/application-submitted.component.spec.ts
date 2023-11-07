import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../testing';
import { mockState } from '../../permit-application/testing/mock-state';
import { SharedModule } from '../../shared/shared.module';
import { PermitIssuanceStore } from '../store/permit-issuance.store';
import { ApplicationSubmittedComponent } from './application-submitted.component';

describe('ApplicationSubmittedComponent', () => {
  let component: ApplicationSubmittedComponent;
  let fixture: ComponentFixture<ApplicationSubmittedComponent>;
  let store: PermitIssuanceStore;
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApplicationSubmittedComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  class Page extends BasePage<ApplicationSubmittedComponent> {
    get paragraphsContent() {
      return this.queryAll('p[class="govuk-body"]').map((p) => p.textContent.trim());
    }
  }

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should properly refere the competent authority', () => {
    expect(page.paragraphsContent[1]).toBe('We’ve sent your application to Environment Agency');
  });
});
