import { SceneInfo, ThreeService } from './../../../../../services/three.service';
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

  private _planet: Planet;

  @ViewChild('rendererContainer') rendererContainer: ElementRef;

  sceneinfo: SceneInfo;


  material: THREE.MeshBasicMaterial = null;
  light: THREE.PointLight = null;
  color = null;

  constructor(private th: ThreeService) {

  }


  ngOnInit(): void {

  }

  ngOnDestroy(): void {
    this.th.removeSceneInfo(this.sceneinfo);
  }

  @Input()
  set planet(planet: Planet) {
    this._planet = planet;
    this.setScene();
  }

  get planet(): Planet {
    return this._planet;
  }

  private setScene() {
    if (this.sceneinfo) {
      this.th.removeSceneInfo(this.sceneinfo);
    }
    const scene = new THREE.Scene();

    const camera = new THREE.PerspectiveCamera(75, 1, 1, 10000);
    camera.position.z = 400;

    const geometry = new THREE.SphereGeometry(200, 15, 10);
    this.material = new THREE.MeshLambertMaterial({color: 0xff0000, wireframe: true, opacity: 0.2, transparent: true});
    const mesh = new THREE.Mesh(geometry, this.material);
    mesh.rotation.x = 0.7;
    mesh.rotation.z = 0.3;
    scene.add(mesh);

    this.light = new THREE.PointLight( 0xffffff, 2, 1000 );
    this.light.position.set( 200, 0, 300 );
    scene.add(this.light);

    this.color = PLANET_COLORS[this.planet.type - 1];
    this.material.color.r = this.color.r;
    this.material.color.g = this.color.g;
    this.material.color.b = this.color.b;

    mesh.scale.x = 0.25 * this.planet.size;
    mesh.scale.y = 0.25 * this.planet.size;
    mesh.scale.z = 0.25 * this.planet.size;

    const planet = this.planet;
    const animate = () => {
      mesh.rotation.y += 0.01 + 0.0005 * planet.orbit;
    };

    this.sceneinfo = new SceneInfo(scene, camera, this.rendererContainer.nativeElement, mesh, animate);

    this.th.addSceneInfo(this.sceneinfo);
  }

  mouseEnter() {
    this.light.intensity = 4;
  }

  mouseOut() {
    this.light.intensity = 2;
  }


}
