import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllProviderMonitoringComponent } from './all-provider-monitoring.component';

describe('AllProviderMonitoringComponent', () => {
  let component: AllProviderMonitoringComponent;
  let fixture: ComponentFixture<AllProviderMonitoringComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllProviderMonitoringComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AllProviderMonitoringComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
