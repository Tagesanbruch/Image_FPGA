//****************************************************************************//
//# @Author: 碎碎思
//# @Date:   2019-10-30 22:03:48
//# @Last Modified by:   zlk
//# @WeChat Official Account: OpenFPGA
//# @Last Modified time: 2019-10-30 22:09:19
//# Description: 
//# @Modification History: 2017-04-22 09:19:50
//# Date			    By			   Version			   Change Description: 
//# ========================================================================= #
//# 2017-04-22 09:19:50 CrazyBingo      V0         
//# ========================================================================= #
//# |                                          								| #
//# |                                OpenFPGA     							| #
//****************************************************************************// 

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
	input 		[3:0]	per_img_mode,		// 0 for RGB888, ......
	input		[7:0]	per_img_red,		//Prepared Image red data to be processed
	input		[7:0]	per_img_green,		//Prepared Image green data to be processed
	input		[7:0]	per_img_blue,		//Prepared Image blue data to be processed

	//Image data has been processd
	output				post_frame_vsync,	//Processed Image data vsync valid signal
	output				post_frame_href,	//Processed Image data href vaild  signal
	output				post_frame_clken,	//Processed Image data output/capture enable clock
	output 		[3:0]	post_img_mode,		// 0 for YCbCr, ......
	output		[7:0]	post_img_Y,			//Processed Image brightness output
	output		[7:0]	post_img_Cb,			//Processed Image blue shading output
	output		[7:0]	post_img_Cr,			//Processed Image red shading output
	output		[7:0]	post_img_red,			//Processed Image red output
	output		[7:0]	post_img_green,			//Processed Image green output
	output		[7:0]	post_img_blue,			//Processed Image blue output
	output 				post_img_bit
);

parameter IMG_HDISP = 12'd1920;
parameter IMG_VDISP = 12'd1080;
parameter IMG_HBLANLK = 11'd5;
parameter IMG_VBLANK = 11'd0;
parameter DELAY_NUM = IMG_HBLANLK  ;


wire				mid1_frame_vsync;
wire				mid1_frame_href;
wire				mid1_frame_clken;
wire 		[3:0]	mid1_img_mode	;
wire		[7:0]	mid1_img_Y		;
wire		[7:0]	mid1_img_Cb	;
wire		[7:0]	mid1_img_Cr	;

//-------------------------------------
//Convert the RGB888 format to YCbCr444 format.
Image_RGB888_YCbCr444	u_VIP_RGB888_YCbCr444
(
	//global clock
	.clk				(clk),					//cmos video pixel clock
	.rst_n				(rst_n),				//system reset

	//Image data prepred to be processd
	.per_frame_vsync	(per_frame_vsync),		//Prepared Image data vsync valid signal
	.per_frame_href		(per_frame_href),		//Prepared Image data href vaild  signal
	.per_frame_clken	(per_frame_clken),		//Prepared Image data output/capture enable clock
	.per_img_red		(per_img_red),			//Prepared Image red data input
	.per_img_green		(per_img_green),		//Prepared Image green data input
	.per_img_blue		(per_img_blue),			//Prepared Image blue data input
	
	//Image data has been processd
	.post_frame_vsync	(mid1_frame_vsync),		//Processed Image frame data valid signal
	.post_frame_href	(mid1_frame_href),		//Processed Image hsync data valid signal
	.post_frame_clken	(mid1_frame_clken),		//Processed Image data output/capture enable clock
	.post_img_Y			(mid1_img_Y),			//Processed Image brightness output
	.post_img_Cb		(mid1_img_Cb),			//Processed Image blue shading output
	.post_img_Cr		(mid1_img_Cr)			//Processed Image red shading output
);

sobel_detector
#(
    .IMG_HDISP(IMG_HDISP),               
    .IMG_VDISP(IMG_VDISP),
	.DELAY_NUM(DELAY_NUM)
) u_sobel_detector
(
    .clk(clk),
    .rst_n(rst_n),
    
    .thresh(8'd48),
    
    //  Image data prepared to be processed
    .per_img_vsync(mid1_frame_vsync),       //  Prepared Image data vsync valid signal
    .per_img_href (mid1_frame_href),       //  Prepared Image data href vaild  signal
    .per_img_clken (mid1_frame_clken),       //  Prepared Image data href vaild  signal
    .per_img_gray (mid1_img_Y),       //  Prepared Image brightness input
    
    //  Image data has been processed
    .post_img_vsync(post_frame_vsync)  ,       //  processed Image data vsync valid signal
    .post_img_href(post_frame_href)   ,       //  processed Image data href vaild  signal
    .post_img_clken(post_frame_clken)   ,       //  processed Image data href vaild  signal
    .post_img_bit(post_img_bit)            //  processed Image brightness output
);
assign post_img_Y = {8{post_img_bit}};
assign post_img_Cb = 0;
assign post_img_Cr = 0;
assign post_img_mode = 'b0;

endmodule
