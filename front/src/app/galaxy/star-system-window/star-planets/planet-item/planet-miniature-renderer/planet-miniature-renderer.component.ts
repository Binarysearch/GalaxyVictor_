import { PLANET_COLORS } from './../../../../galaxy-constants';
import { Planet } from './../../../../../game-objects/planet';
import { Component, OnInit, ViewChild, ElementRef, Input, OnDestroy } from '@angular/core';
import * as THREE from 'three';

@Component({
  selector: 'app-planet-miniature-renderer',
  templateUrl: './planet-miniature-renderer.component.html',
  styleUrls: ['./planet-miniature-renderer.component.css']
})
export class PlanetMiniatureRendererComponent implements OnInit, OnDestroy {


  @ViewChild('rendererContainer') rendererContainer: ElementRef;

  @Input() planet: Planet;

  renderer = new THREE.WebGLRenderer({ alpha: true });
  scene = null;
  camera = null;
  mesh = null;
  material: THREE.MeshBasicMaterial = null;
  light: THREE.PointLight = null;
  color = null;

  constructor() {
    this.scene = new THREE.Scene();

    this.camera = new THREE.PerspectiveCamera(75, 1, 1, 10000);
    this.camera.position.z = 400;

    const geometry = new THREE.SphereGeometry(200, 15, 10);
    this.material = new THREE.MeshLambertMaterial({color: 0xff0000, wireframe: true, opacity: 0.2, transparent: true});
    this.mesh = new THREE.Mesh(geometry, this.material);
    this.mesh.rotation.x = 0.7;
    this.mesh.rotation.z = 0.3;
    this.scene.add(this.mesh);

    this.light = new THREE.PointLight( 0xffffff, 2, 1000 );
    this.light.position.set( 200, 0, 300 );
    this.scene.add(this.light);
  }

  ngOnInit(): void {
    this.renderer.setSize(75, 75);
    this.rendererContainer.nativeElement.appendChild(this.renderer.domElement);
    this.animate();
    this.color = PLANET_COLORS[this.planet.type - 1];
    this.material.color.r = this.color.r;
    this.material.color.g = this.color.g;
    this.material.color.b = this.color.b;

    this.mesh.scale.x = 0.25 * this.planet.size;
    this.mesh.scale.y = 0.25 * this.planet.size;
    this.mesh.scale.z = 0.25 * this.planet.size;
  }

  ngOnDestroy(): void {
    this.renderer.forceContextLoss();
    this.renderer.context = null;
    this.renderer.domElement = null;
    this.renderer.dispose();
    this.renderer = null;
  }

  animate() {
    window.requestAnimationFrame(() => {
      if (this.renderer) {
        this.animate();
      }
    });
    this.mesh.rotation.y += 0.01 + 0.0005 * this.planet.orbit;
    this.renderer.render(this.scene, this.camera);
  }

  mouseEnter() {
    this.light.intensity = 4;
  }

  mouseOut() {
    this.light.intensity = 2;
  }


}
