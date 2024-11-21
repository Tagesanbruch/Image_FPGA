

`timescale 1ns/1ns
module TOP
(
	//global clock
	input				clk,  				//cmos video pixel clock
	input				rst_n,				//global reset

	//Image data prepred to be processd
	input				per_frame_vsync,	//Prepared Image data vsync valid signal
	input				per_frame_href,		//Prepared Image data href vaild  signal
	input				per_frame_clken,	//Prepared Image data output/capture enable clock
	input 		[3:0]	per_img_mode,		// 
	input		[7:0]	per_img_raw,		//Prepared Image red data to be processed
	input		[7:0]	per_img_red,		//Prepared Image red data to be processed
	input		[7:0]	per_img_green,		//Prepared Image green data to be processed
	input		[7:0]	per_img_blue,		//Prepared Image blue data to be processed

	//Image data has been processd
	output				post_frame_vsync,	//Processed Image data vsync valid signal
	output				post_frame_href,	//Processed Image data href vaild  signal
	output				post_frame_clken,	//Processed Image data output/capture enable clock
	output 		[3:0]	post_img_mode,		// 0 for YCbCr, 1 for rgb 2 for raw......
	output		[7:0]	post_img_Y,			//Processed Image brightness output
	output		[7:0]	post_img_Cb,			//Processed Image blue shading output
	output		[7:0]	post_img_Cr,			//Processed Image red shading output
	output		[7:0]	post_img_red,
	output		[7:0]	post_img_green,
	output		[7:0]	post_img_blue,
	output		[7:0]	post_img_raw,
	output 				post_img_bit
);

parameter IMG_HDISP=1920;
parameter IMG_VDISP=1080;
parameter IMG_HBLANLK=11'd5;
parameter DELAY_NUM=IMG_HBLANLK;
parameter BAYER =3 ;//0:RGGB 1:GRBG 2:GBRG 3:BGGR

assign post_img_mode = 1; 


//___________________ 拜耳降噪 (可选择的高斯滤波器)___________
wire bnr_href ;
wire bnr_vsync;
wire [7:0] bnr_raw  ;

isp_bnr
#(
	.BITS  (8   ),
	.WIDTH (IMG_HDISP),
	.HEIGHT(IMG_VDISP ) ,
	.BAYER (BAYER   )//0:RGGB 1:GRBG 2:GBRG 3:BGGR
) isp_bnr_u1
(
	.pclk     (clk     ),
	.rst_n	  (rst_n	  ),
	.nr_level (4'd0 ),           //0:NoNR 1-4:NRLevel
	.in_href  (per_frame_href  ),
	.in_vsync (per_frame_vsync ),
	.in_raw   (per_img_raw   ),
	.out_href (bnr_href ),
	.out_vsync(bnr_vsync),
	.out_raw  (bnr_raw  )
);

//____________________RAW(BGGR) 2 RGB888_______________
wire rgb_vsync;
wire rgb_href ;
wire [7:0] rgb_red  ;
wire [7:0] rgb_green;
wire [7:0] rgb_blue ;
raw8_2_rgb888#(
	.H_SIZE(IMG_HDISP),    
	.V_SIZE(IMG_VDISP)
)  raw8_2_rgb888_u1
(
    //global clock
.I_clk     (clk    			),      
.I_rst_n   (rst_n  			),      
.I_raw_vs  (bnr_vsync 		),      
.I_raw_de  (bnr_href		),      
.I_raw_data(bnr_raw		),     
.O_rgb_vs  (rgb_vsync),  
.O_rgb_de  (rgb_href ),  
.O_rgb_r   (rgb_red  	),  
.O_rgb_g   (rgb_green  ),  
.O_rgb_b   (rgb_blue   )
);

//___________________rgb2yuv_________________
wire 	   csc_href; 	 
wire 	   csc_vsync;	 
wire [7:0] csc_y ;	 	 
wire [7:0] csc_u ;	 	 
wire [7:0] csc_v ;  	 
isp_csc #(
	.BITS  (8),
	.WIDTH (IMG_HDISP),
	.HEIGHT(IMG_VDISP) 
) isp_csc_u1
(
	.pclk     (clk     	 ),
	.rst_n	 (rst_n	 	 ),
	.in_href (rgb_href	 ),
	.in_vsync(rgb_vsync	 ),
	.in_r	 (rgb_red  	 ),
	.in_g	 (rgb_green	 ),
	.in_b	 (rgb_blue 	 ),
	.out_href (csc_href 	 ),
	.out_vsync(csc_vsync	 ),
	.out_y	 (csc_y	 	 ),
	.out_u	 (csc_u	 	 ),
	.out_v    (csc_v   	 )
);

//___________________2dnr_________________
wire [7*7*5-1:0]  space_kernel ;
wire [9*8-1:0] color_curve_x ;
wire [9*5-1:0]    color_curve_y ;
assign  space_kernel = {
			5'd28, 5'd29, 5'd29, 5'd30, 5'd29, 5'd29, 5'd28,
			5'd29, 5'd30, 5'd30, 5'd30, 5'd30, 5'd30, 5'd29,
			5'd29, 5'd30, 5'd31, 5'd31, 5'd31, 5'd30, 5'd29,
			5'd30, 5'd30, 5'd31, 5'd31, 5'd31, 5'd30, 5'd30,
			5'd29, 5'd30, 5'd31, 5'd31, 5'd31, 5'd30, 5'd29,
			5'd29, 5'd30, 5'd30, 5'd30, 5'd30, 5'd30, 5'd29,
			5'd28, 5'd29, 5'd29, 5'd30, 5'd29, 5'd29, 5'd28
		};
assign color_curve_x = {8'd30, 8'd26, 8'd23, 8'd20, 8'd17, 8'd13, 8'd10, 8'd6 , 8'd3 };
assign color_curve_y = {5'd0 , 5'd1 , 5'd2 , 5'd4 , 5'd7 , 5'd13, 5'd19, 5'd26, 5'd30};


wire nr2d_href	;
wire nr2d_vsync	;
wire [7:0] nr2d_y;
wire [7:0] nr2d_u;
wire [7:0] nr2d_v;
wire [7:0] nr2d_u_tmp;
wire [7:0] nr2d_v_tmp;
isp_2dnr#(
	.BITS 		 (8),
	.WIDTH 		 (IMG_HDISP),
	.HEIGHT 	 (IMG_VDISP),
	.WEIGHT_BITS (5)
) isp_2dnr_u1
(
	.pclk			(clk			),
	.rst_n			(rst_n			),
	.space_kernel	(space_kernel	), //空域卷积核(7x7)
	.color_curve_x	(color_curve_x	),//值域卷积核拟合曲线横坐标(9个坐标点)
	.color_curve_y	(color_curve_y	),//值域卷积核拟合曲线纵坐标(9个坐标点)
	.in_href		(csc_href		),
	.in_vsync		(csc_vsync		),
	.in_data		(csc_y  		),
	.out_href		(nr2d_href		),
	.out_vsync		(nr2d_vsync		),
	.out_data		(nr2d_y		)
);
shift_register #(8, IMG_HDISP, 6) nr2d_shift_u (clk, csc_href, csc_u, nr2d_u_tmp, ); //由于2dnr内部使用6行linebuffer,这里UV会早Y量6行数据,造成亮度与色度不匹配问题
shift_register #(8, IMG_HDISP, 6) nr2d_shift_v (clk, csc_href, csc_v, nr2d_v_tmp, );
data_delay #(8, (11+8+2*5+6)) nr2d_delay_u (clk, rst_n, nr2d_u_tmp, nr2d_u);
data_delay #(8, (11+8+2*5+6)) nr2d_delay_v (clk, rst_n, nr2d_v_tmp, nr2d_v);

//____________________yuv2rgb_______________________
	
Image_YCbCr444_RGB888 u_Image_YCbCr444_RGB888
(
	.clk             (clk             ),  
	.rst_n			 (rst_n			 ),	
	.per_frame_vsync (csc_vsync ),	
	.per_frame_href	 (csc_href	 ),	
	.per_frame_clken ( per_frame_clken),	
	.per_img_Y		 (csc_y		 ),	
	.per_img_Cb		 (csc_u		 ),	
	.per_img_Cr		 (csc_v		 ),	
	.post_frame_vsync(post_frame_vsync),	
	.post_frame_href (post_frame_href ),	
	.post_frame_clken(post_frame_clken),	
	.post_img_red	 (post_img_red	 ),	
	.post_img_green	 (post_img_green	 ),	
	.post_img_blue	 (post_img_blue	 )	
);





endmodule
