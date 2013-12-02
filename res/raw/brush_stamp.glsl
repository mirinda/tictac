precision highp float;        // Set the default precision to medium. We don't need as high of a
                                // precision in the fragment shader.

uniform sampler2D u_Texture;    // The input texture.

varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

uniform mediump float u_Ratios[14];
uniform mediump float u_Alphas[14];
uniform lowp vec4 u_Color;
uniform mediump float u_RealRadius;

// The entry point for our fragment shader.
void main()
{
	highp float dist =2.0 *distance(vec2(0.5),v_TexCoordinate)*u_RealRadius;
	if(dist< u_Ratios[0]){
    	gl_FragColor =vec4(u_Color.rgb,1.0);//vec4(1.0,1.0,1.0,1.0);  vec4(1.0,v_TexCoordinate[0],v_TexCoordinate[1],1.0);
    	return;
    }
    if(dist < u_Ratios[1]){
    	highp float koef=(dist-u_Ratios[0])/(u_Ratios[1]-u_Ratios[0]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[0]*(1.0-koef) + u_Alphas[1]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[2]){
    	highp float koef=(dist-u_Ratios[1])/(u_Ratios[2]-u_Ratios[1]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[1]*(1.0-koef) + u_Alphas[2]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[3]){
    	highp float koef=(dist-u_Ratios[2])/(u_Ratios[3]-u_Ratios[2]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[2]*(1.0-koef) + u_Alphas[3]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[4]){
    	highp float koef=(dist-u_Ratios[3])/(u_Ratios[4]-u_Ratios[3]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[3]*(1.0-koef) + u_Alphas[4]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[5]){
    	highp float koef=(dist-u_Ratios[4])/(u_Ratios[5]-u_Ratios[4]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[4]*(1.0-koef) + u_Alphas[5]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[6]){
    	highp float koef=(dist-u_Ratios[5])/(u_Ratios[6]-u_Ratios[5]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[5]*(1.0-koef) + u_Alphas[6]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[7]){
    	highp float koef=(dist-u_Ratios[6])/(u_Ratios[7]-u_Ratios[6]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[6]*(1.0-koef) + u_Alphas[7]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[8]){
    	highp float koef=(dist-u_Ratios[7])/(u_Ratios[8]-u_Ratios[7]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[7]*(1.0-koef) + u_Alphas[8]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[9]){
    	highp float koef=(dist-u_Ratios[8])/(u_Ratios[9]-u_Ratios[8]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[8]*(1.0-koef) + u_Alphas[9]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[10]){
    	highp float koef=(dist-u_Ratios[9])/(u_Ratios[10]-u_Ratios[9]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[9]*(1.0-koef) + u_Alphas[10]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[11]){
    	highp float koef=(dist-u_Ratios[10])/(u_Ratios[11]-u_Ratios[10]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[10]*(1.0-koef) + u_Alphas[11]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[12]){
    	highp float koef=(dist-u_Ratios[11])/(u_Ratios[12]-u_Ratios[0]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[11]*(1.0-koef) + u_Alphas[12]*koef);//)*255),255,  0, 0);
		return;
    }
    if(dist < u_Ratios[13]){
    	highp float koef=(dist-u_Ratios[12])/(u_Ratios[13]-u_Ratios[12]);
		gl_FragColor=vec4(u_Color.rgb,u_Alphas[12]*(1.0-koef) + u_Alphas[13]*koef);//)*255),255,  0, 0);
		return;
    }
    
    gl_FragColor = vec4(0.0,0.0,0.0,0.0);
    //if()
    
  }