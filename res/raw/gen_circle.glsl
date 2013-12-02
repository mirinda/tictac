precision highp float;        // Set the default precision to medium. We don't need as high of a
                                // precision in the fragment shader.

uniform sampler2D u_Texture;    // The input texture.

varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

void main(){
	if(distance(v_TexCoordinate,vec2(0.5))>0.3){
		gl_FragColor=vec4(1.0,1.0,0.0,1.0);
	}else{
    	gl_FragColor = vec4(0.0,0.0,0.0,1.0);
    }
}