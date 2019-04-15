import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanetMiniatureRendererComponent } from './planet-miniature-renderer.component';

describe('PlanetMiniatureRendererComponent', () => {
  let component: PlanetMiniatureRendererComponent;
  let fixture: ComponentFixture<PlanetMiniatureRendererComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlanetMiniatureRendererComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlanetMiniatureRendererComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
