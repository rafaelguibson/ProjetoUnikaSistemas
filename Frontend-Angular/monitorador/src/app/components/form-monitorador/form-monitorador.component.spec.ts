import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormMonitoradorComponent } from './form-monitorador.component';

describe('FormMonitoradorComponent', () => {
  let component: FormMonitoradorComponent;
  let fixture: ComponentFixture<FormMonitoradorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormMonitoradorComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FormMonitoradorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
