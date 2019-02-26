import { Camera } from './camera';
import { StarSystem } from '../entities/star-system';
import { Renderer } from './renderer';
import { ShaderProgramCompiler } from './gl/shader-program-compiler';
import { STAR_SYSTEM_VS_SOURCE, STAR_SYSTEM_FS_SOURCE } from './gl/shaders/star-system-shader';

export class StarRenderer implements Renderer {
    program: WebGLShader;
    vao: WebGLVertexArrayObjectOES;
    timeUniformLocation: WebGLUniformLocation;
    hoverUniformLocation: WebGLUniformLocation;
    aspectUniformLocation: WebGLUniformLocation;
    scaleUniformLocation: WebGLUniformLocation;
    zoomUniformLocation: WebGLUniformLocation;
    positionUniformLocation: WebGLUniformLocation;
    colorUniformLocation: WebGLUniformLocation;
    civilizationColorUniformLocation: WebGLUniformLocation;

    constructor(private camera: Camera, private shaderCompiler: ShaderProgramCompiler) {}

    setup(gl: any) {
        this.program = this.shaderCompiler.createShaderProgram(gl, STAR_SYSTEM_VS_SOURCE, STAR_SYSTEM_FS_SOURCE);

        this.vao = (gl as any).createVertexArray();
        (gl as any).bindVertexArray(this.vao);

        gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer());
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([-1, 1, 0,  -1, -1, 0,  1, -1, 0,  1, 1, 0, -1, 1, 0, 1, -1, 0]), gl.STATIC_DRAW);

        const coord = gl.getAttribLocation(this.program, 'position');
        gl.vertexAttribPointer(coord, 3, gl.FLOAT, false, 0, 0);
        gl.enableVertexAttribArray(coord);

        this.timeUniformLocation = gl.getUniformLocation(this.program, 'time');
        this.hoverUniformLocation = gl.getUniformLocation(this.program, 'hover');
        this.aspectUniformLocation = gl.getUniformLocation(this.program, 'aspect');
        this.scaleUniformLocation = gl.getUniformLocation(this.program, 'scale');
        this.zoomUniformLocation = gl.getUniformLocation(this.program, 'zoom');
        this.positionUniformLocation = gl.getUniformLocation(this.program, 'pos');
        this.colorUniformLocation = gl.getUniformLocation(this.program, 'color');
        this.civilizationColorUniformLocation = gl.getUniformLocation(this.program, 'civilizationColor');
    }

    prepareRender(gl: any) {
        const zoom = this.camera.zoom;
        const aspect = this.camera.aspectRatio;
        gl.enable(gl.BLEND);
        gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
        gl.useProgram(this.program);
        gl.bindVertexArray(this.vao);

        const time = (656454 % (Math.PI * 2000)) * 0.001;

        gl.uniform1f(this.timeUniformLocation, time);
        gl.uniform1f(this.zoomUniformLocation, zoom);
        gl.uniform1f(this.aspectUniformLocation, aspect);
    }

    render(gl: any, star: StarSystem) {
        const zoom = this.camera.zoom;
        const nz = 4;

        // z de estrella
        const starz = 0;

        if (nz * zoom < starz) {
            return;
        }


        gl.uniform3f(this.civilizationColorUniformLocation, 1, 1, 1);
        gl.uniform1f(this.scaleUniformLocation, this.getElementRenderScale(star));
        gl.uniform2f(this.positionUniformLocation, star.x - this.camera.x, star.y - this.camera.y);
        gl.uniform3f(this.colorUniformLocation, 1, 1, 1);
        gl.uniform1f(this.hoverUniformLocation, 0.0);

        gl.drawArrays(gl.TRIANGLES, 0, 6);

    }

    getElementRenderScale(ss: StarSystem): number {
      const zoom = this.camera.zoom;
      const nz = 4;

      // z de estrella
      const starz = 0;

      const s = ss.size * Math.min(Math.max((nz * zoom - starz) / (nz * zoom), 0), 1);
      const scale = (0.015 * s + 0.01) / zoom + 0.0001;
      return scale;
    }
}
