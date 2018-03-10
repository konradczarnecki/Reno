import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HashAnimationComponent } from './hash-animation.component';

describe('HashAnimationComponent', () => {
  let component: HashAnimationComponent;
  let fixture: ComponentFixture<HashAnimationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HashAnimationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HashAnimationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
