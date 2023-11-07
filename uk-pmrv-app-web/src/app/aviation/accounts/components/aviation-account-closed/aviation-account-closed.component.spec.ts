import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { BasePage } from '@testing';

import { mockedAccountClosed } from '../../testing/mock-data';
import { AviationAccountClosedComponent } from './aviation-account-closed.component';

describe('AviationAccountClosedComponent', () => {
  let component: AviationAccountClosedComponent;
  let fixture: ComponentFixture<AviationAccountClosedComponent>;
  let page: Page;

  class Page extends BasePage<AviationAccountClosedComponent> {
    get heading() {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AviationAccountClosedComponent, GovukDatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(AviationAccountClosedComponent);
    component = fixture.componentInstance;
    component.accountInfo = mockedAccountClosed.aviationAccount;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the heading', () => {
    expect(page.heading.map((el) => el.textContent.trim())).toEqual(['Account closed']);
  });

  it('should show account closed details when existing', () => {
    const closureReason = fixture.debugElement.query(By.css('#aviation-account-closureReason > dd')).nativeElement;
    const closedByName = fixture.debugElement.query(By.css('#aviation-account-closedByName > dd')).nativeElement;
    const closingDate = fixture.debugElement.query(By.css('#aviation-account-closingDate > dd')).nativeElement;

    expect(closureReason.textContent.trim()).toStrictEqual('reason provided');
    expect(closedByName.textContent.trim()).toStrictEqual('Regulator A Name');
    expect(closingDate.textContent).toStrictEqual('5 May 2023, 4:00am');
  });
});
