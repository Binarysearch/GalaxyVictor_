import { Injectable } from '@angular/core';
import * as THREE from 'three';

export class SceneInfo {

  constructor(
    public scene: THREE.Scene,
    public camera: THREE.PerspectiveCamera,
    public elem: HTMLElement,
    public mesh: THREE.Mesh,
    public animate: () => void,
  ) { }

}

@Injectable({
  providedIn: 'root'
})
export class ThreeService {

  private renderer: THREE.WebGLRenderer;
  private _sceneInfos: SceneInfo[] = [];

  constructor() {
    this.render();
  }

  public setup(canvas: HTMLCanvasElement) {
    this.destroyContext();
    this.renderer = new THREE.WebGLRenderer({ canvas: canvas, alpha: true });
  }

  private destroyContext() {
    if (this.renderer) {
      this._sceneInfos = [];
      this.renderer.forceContextLoss();
      this.renderer.context = null;
      this.renderer.domElement = null;
      this.renderer.dispose();
      this.renderer = null;
    }
  }

  resizeRendererToDisplaySize(): boolean {
    const canvas = this.renderer.domElement;
    const width = canvas.clientWidth;
    const height = canvas.clientHeight;
    const needResize = canvas.width !== width || canvas.height !== height;
    if (needResize) {
      this.renderer.setSize(width, height, false);
    }
    return needResize;
  }

  rendenerSceneInfo(sceneInfo: SceneInfo) {
    const {scene, camera, elem} = sceneInfo;

    const {left, top, width, height} = elem.getBoundingClientRect();

    const eleft = (left - this.renderer.domElement.getBoundingClientRect().left);
    const bottom = (top - this.renderer.domElement.getBoundingClientRect().top) + height;

    camera.aspect = width / height;
    camera.updateProjectionMatrix();

    const positiveYUpBottom = this.renderer.domElement.clientHeight - bottom;
    this.renderer.setScissor(eleft, positiveYUpBottom, width, height);
    this.renderer.setViewport(eleft, positiveYUpBottom, width, height);

    this.renderer.render(scene, camera);
  }

  public addSceneInfo(sceneInfo: SceneInfo) {
    this._sceneInfos.push(sceneInfo);
  }

  public removeSceneInfo(sceneInfo: SceneInfo) {
    if (this._sceneInfos.indexOf(sceneInfo) > -1) {
      this._sceneInfos.splice(this._sceneInfos.indexOf(sceneInfo), 1);
    }
  }

  render() {
    window.requestAnimationFrame(() => {
      this.render();
    });

    if (this.renderer) {
      this.resizeRendererToDisplaySize();

      this.renderer.setScissorTest(false);
      this.renderer.clear(true, true);
      this.renderer.setScissorTest(true);

      this._sceneInfos.forEach(si => {
        si.animate();
        this.rendenerSceneInfo(si);
      });
    }
  }
}
