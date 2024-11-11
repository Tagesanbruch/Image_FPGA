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
	input 				clk_div_2,
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
	output 				post_img_bit
);


// // clk divide by 3
// wire clk_div3;
// clock_div_3 u_clock_div_3
// (
// 	.clk(clk),
// 	.rst_n(rst_n),
// 	.clk_div3(clk_div3)
// );


// Define parameters for nearest_interpolation
// localparam C_SRC_IMG_WIDTH  = 11'd640;
// localparam C_SRC_IMG_HEIGHT = 11'd480;
// localparam C_DST_IMG_WIDTH  = 11'd1024;
// localparam C_DST_IMG_HEIGHT = 11'd768;
// localparam C_X_RATIO        = 16'd40960; // floor(C_SRC_IMG_WIDTH/C_DST_IMG_WIDTH*2^16)
// localparam C_Y_RATIO        = 16'd40960; // floor(C_SRC_IMG_HEIGHT/C_DST_IMG_HEIGHT*2^16)

localparam C_SRC_IMG_WIDTH  = 11'd640;
localparam C_SRC_IMG_HEIGHT = 11'd480;
localparam C_DST_IMG_WIDTH  = 11'd1600;
localparam C_DST_IMG_HEIGHT = 11'd900;
localparam C_X_RATIO        = 16'd26214; // floor(C_SRC_IMG_WIDTH/C_DST_IMG_WIDTH*2^16)
localparam C_Y_RATIO        = 16'd34952; // floor(C_SRC_IMG_HEIGHT/C_DST_IMG_HEIGHT*2^16)


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
	.clk				(clk_div_2),					//cmos video pixel clock
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

// sobel_detector
// #(
//     .IMG_HDISP(11'd640),                //  640*480
//     .IMG_VDISP(11'd480)
// ) u_sobel_detector
// (
//     .clk(clk),
//     .rst_n(rst_n),
    
//     .thresh(8'd48),
    
//     //  Image data prepared to be processed
//     .per_img_vsync(mid1_frame_vsync),       //  Prepared Image data vsync valid signal
//     .per_img_href (mid1_frame_href),       //  Prepared Image data href vaild  signal
//     .per_img_clken (mid1_frame_clken),       //  Prepared Image data href vaild  signal
//     .per_img_gray (mid1_img_Y),       //  Prepared Image brightness input
    
//     //  Image data has been processed
//     .post_img_vsync(post_frame_vsync)  ,       //  processed Image data vsync valid signal
//     .post_img_href(post_frame_href)   ,       //  processed Image data href vaild  signal
//     .post_img_clken(post_frame_clken)   ,       //  processed Image data href vaild  signal
//     .post_img_bit(post_img_bit)            //  processed Image brightness output
// );
// assign post_img_Y = {8{post_img_bit}};
// assign post_img_Cb = 0;
// assign post_img_Cr = 0;

// module nearest_interpolation
// #(
//     parameter C_SRC_IMG_WIDTH  = 11'd640    ,
//     parameter C_SRC_IMG_HEIGHT = 11'd480    ,
//     parameter C_DST_IMG_WIDTH  = 11'd1024   ,
//     parameter C_DST_IMG_HEIGHT = 11'd768    ,
//     parameter C_X_RATIO        = 16'd40960  ,                           //  floor(C_SRC_IMG_WIDTH/C_DST_IMG_WIDTH*2^16)
//     parameter C_Y_RATIO        = 16'd40960                              //  floor(C_SRC_IMG_HEIGHT/C_DST_IMG_HEIGHT*2^16)
// )
// (
//     input  wire                 clk_in1         ,
//     input  wire                 clk_in2         ,
//     input  wire                 rst_n           ,
    
//     //  Image data prepared to be processed
//     input  wire                 per_img_vsync   ,                       //  Prepared Image data vsync valid signal
//     input  wire                 per_img_href    ,                       //  Prepared Image data href vaild  signal
//     input  wire     [7:0]       per_img_gray    ,                       //  Prepared Image brightness input
    
//     //  Image data has been processed
//     output reg                  post_img_vsync  ,                       //  processed Image data vsync valid signal
//     output reg                  post_img_href   ,                       //  processed Image data href vaild  signal
//     output wire     [7:0]       post_img_gray                           //  processed Image brightness output
// );

// Instantiate nearest_interpolation module
nearest_interpolation #(
    .C_SRC_IMG_WIDTH(C_SRC_IMG_WIDTH),
    .C_SRC_IMG_HEIGHT(C_SRC_IMG_HEIGHT),
    .C_DST_IMG_WIDTH(C_DST_IMG_WIDTH),
    .C_DST_IMG_HEIGHT(C_DST_IMG_HEIGHT),
    .C_X_RATIO(C_X_RATIO),
    .C_Y_RATIO(C_Y_RATIO)
) u_nearest_interpolation (
    .clk_in1(clk_div_2),           // Use clk_div3 as clk_in1
    .clk_in2(clk),                // Use clk as clk_in2
    .rst_n(rst_n),                // System reset

    // Image data prepared to be processed
    .per_img_vsync(mid1_frame_vsync), // Prepared Image data vsync valid signal
    .per_img_href(mid1_frame_href),   // Prepared Image data href valid signal
    .per_img_gray(mid1_img_Y),        // Prepared Image brightness input

    // Image data has been processed
    .post_img_vsync(post_frame_vsync), // Processed Image data vsync valid signal
    .post_img_href(post_frame_href),   // Processed Image data href valid signal
    .post_img_gray(post_img_Y)         // Processed Image brightness output
);

// Assign Cb and Cr to zero as they are not used in nearest_interpolation
assign post_img_Cb = 0;
assign post_img_Cr = 0;
endmodule
