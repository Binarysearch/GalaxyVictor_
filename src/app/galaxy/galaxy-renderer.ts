import { Injectable } from '@angular/core';
import { StarRenderer } from './star-renderer';
import { Camera } from './camera';
import { Renderer } from './renderer';
import { ShaderProgramCompiler } from './gl/shader-program-compiler';
import { CoreService } from '../services/core.service';

@Injectable({
  providedIn: 'root'
})
export class GalaxyRenderer implements Renderer {

  private gl: any;
  private animate: boolean;
  private starRenderer: StarRenderer;
  private camera: Camera;

  constructor(private core: CoreService, private shaderCompiler: ShaderProgramCompiler) {
    this.camera = new Camera();
    this.animate = true;
    this.starRenderer = new StarRenderer(this.camera, shaderCompiler);
    const animate = () => {
      if (this.animate) {
        window.requestAnimationFrame(animate);
        if (this.gl) {
          this.update();
          this.render(this.gl);
        }
      }
    };
    animate();
  }

  setup(gl: any) {
    this.animate = false;
    this.gl = gl;
    this.starRenderer.setup(gl);
    this.animate = true;
    gl.clearColor(0, 0, 0, 1);
  }

  prepareRender() {}

  update() {
    this.camera.update();
  }

  render(gl: any) {
      gl.clear(gl.COLOR_BUFFER_BIT);
      this.starRenderer.prepareRender(gl);
      this.core.starSystems.forEach(starSystem => {
          this.starRenderer.render(gl, starSystem);
      });
  }

  setViewport(width: number, height: number) {
      this.gl.viewport(0, 0, width, height);
      this.camera.aspectRatio = width / height;
  }

  zoomEvent(delta: number): any {
    if (delta < 0) {
      this.camera.zoomIn();
    } else {
      this.camera.zoomOut();
    }
  }

}
