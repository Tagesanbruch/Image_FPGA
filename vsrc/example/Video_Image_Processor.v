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
	output		[7:0]	post_img_raw,			//Processed Image raw output
	output 				post_img_bit
);


//-------------------------------------
//Convert the RGB888 format to YCbCr444 format.
isp_csc	u_VIP_RGB888_YCbCr444
(
	//global clock
	.pclk				(clk),					//cmos video pixel clock
	.rst_n				(rst_n),				//system reset

	//Image data prepred to be processd
	.in_vsync	(per_frame_vsync),		//Prepared Image data vsync valid signal
	.in_href		(per_frame_href),		//Prepared Image data href vaild  signal
	// .per_frame_clken	(per_frame_clken),		//Prepared Image data output/capture enable clock
	.in_r (per_img_red),			//Prepared Image red data input
	.in_g		(per_img_green),		//Prepared Image green data input
	.in_b	(per_img_blue),			//Prepared Image blue data input
	
	//Image data has been processd
	.out_vsync	(post_frame_vsync),		//Processed Image frame data valid signal
	.out_href	(post_frame_href),		//Processed Image hsync data valid signal
	// .post_frame_clken	(post_frame_clken),		//Processed Image data output/capture enable clock
	.out_y			(post_img_Y),			//Processed Image brightness output
	.out_u		(post_img_Cb),			//Processed Image blue shading output
	.out_v		(post_img_Cr)			//Processed Image red shading output
);

assign post_img_mode = 0;

endmodule
