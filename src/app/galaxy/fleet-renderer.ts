import { Camera } from './camera';
import { Renderer } from './renderer';
import { ShaderProgramCompiler } from './gl/shader-program-compiler';
import { FLEET_VS_SOURCE, FLEET_FS_SOURCE } from './gl/shaders/fleet-shader';
import { FLEET_VERTICES } from './gl/shapes/fleet-vertices';
import { Fleet } from '../game-objects/fleet';

export class FleetRenderer implements Renderer {
  program: WebGLShader;
  vao: WebGLVertexArrayObjectOES;
  aspectUniformLocation: WebGLUniformLocation;
  scaleUniformLocation: WebGLUniformLocation;
  zoomUniformLocation: WebGLUniformLocation;
  positionUniformLocation: WebGLUniformLocation;
  angleUniformLocation: WebGLUniformLocation;
  colorUniformLocation: WebGLUniformLocation;

  constructor(private camera: Camera, private shaderCompiler: ShaderProgramCompiler) {}

  setup(gl: any) {
    this.program = this.shaderCompiler.createShaderProgram(gl, FLEET_VS_SOURCE, FLEET_FS_SOURCE);

    this.vao = (gl as any).createVertexArray();
    (gl as any).bindVertexArray(this.vao);

    gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer());
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(FLEET_VERTICES), gl.STATIC_DRAW);

    const coord = gl.getAttribLocation(this.program, 'position');
    gl.vertexAttribPointer(coord, 2, gl.FLOAT, false, 0, 0);
    gl.enableVertexAttribArray(coord);

    this.angleUniformLocation = gl.getUniformLocation(this.program, 'angle');
    this.aspectUniformLocation = gl.getUniformLocation(this.program, 'aspect');
    this.scaleUniformLocation = gl.getUniformLocation(this.program, 'scale');
    this.zoomUniformLocation = gl.getUniformLocation(this.program, 'zoom');
    this.positionUniformLocation = gl.getUniformLocation(this.program, 'pos');
    this.colorUniformLocation = gl.getUniformLocation(this.program, 'color');
  }

  prepareRender(gl: any) {
    const zoom = this.camera.zoom;
    const aspect = this.camera.aspectRatio;
    gl.enable(gl.BLEND);
    gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
    gl.useProgram(this.program);
    gl.bindVertexArray(this.vao);


    gl.uniform1f(this.zoomUniformLocation, zoom);
    gl.uniform1f(this.aspectUniformLocation, aspect);
  }

  render(gl: any, fleet: Fleet) {
    const angle = fleet.angle;

    gl.uniform1f(this.scaleUniformLocation, this.getElementRenderScale(fleet));
    gl.uniform2f(this.positionUniformLocation, fleet.x - this.camera.x, fleet.y - this.camera.y);
    gl.uniform1f(this.angleUniformLocation, -angle);
    gl.uniform3f(this.colorUniformLocation, 1, 1, 0);

    gl.drawArrays(gl.TRIANGLES, 0, 90);
  }

  getElementRenderScale(f: Fleet): number {
    const zoom = this.camera.zoom;
    const scale = (0.02) / zoom + (0.0005);
    return 0;//scale;
  }
}
