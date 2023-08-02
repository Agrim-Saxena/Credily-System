import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnterpriseUserComponent } from './enterprise-user.component';

describe('EnterpriseUserComponent', () => {
  let component: EnterpriseUserComponent;
  let fixture: ComponentFixture<EnterpriseUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EnterpriseUserComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnterpriseUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
