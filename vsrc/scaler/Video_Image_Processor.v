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
	output 				post_img_bit
);

parameter IMG_HDISP = 12'd1920;
parameter IMG_VDISP = 12'd1080;
parameter IMG_HBLANLK = 11'd5;
parameter IMG_VBLANK = 11'd0;
parameter DELAY_NUM = IMG_HBLANLK  ;

parameter INPUT_WIDTH = 640;
parameter INPUT_HEIGHT = 480;
parameter OUTPUT_WIDTH = 1024;
parameter OUTPUT_HEIGHT = 768;

wire				scaled_frame_vsync;
wire				scaled_frame_href;
wire				scaled_frame_clken;
wire		[7:0]	scaled_img_Y;
wire		[7:0]	scaled_img_Cb;
wire		[7:0]	scaled_img_Cr;

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

//-------------------------------------
// Instantiate the streamScaler module

reg start_pulse;
always @(posedge clk or negedge rst_n) begin
    if (!rst_n)
        start_pulse <= 1'b0;
    else
        start_pulse <= mid1_frame_vsync && !start_pulse;
end
wire nextDin;
reg nextDin_reg;
reg  nextDout;
always @(posedge clk or negedge rst_n) begin
	if (!rst_n) begin
		nextDin_reg <= 1'b0;
		nextDout <= 1'b0;
	end
	else begin
		nextDin_reg <= nextDin;
		nextDout <= !nextDin_reg && nextDin; //negedge nextDin
	end
end
streamScaler #(
    .DATA_WIDTH(8),
    .CHANNELS(1),
    .DISCARD_CNT_WIDTH(8),
    .INPUT_X_RES_WIDTH(11),
    .INPUT_Y_RES_WIDTH(11),
    .OUTPUT_X_RES_WIDTH(11),
    .OUTPUT_Y_RES_WIDTH(11),
    .FRACTION_BITS(8),
    .SCALE_INT_BITS(4),
    .SCALE_FRAC_BITS(14),
    .BUFFER_SIZE(4)
) u_streamScaler (
    .clk(clk),
    .rst(~rst_n),

    // Input interface
    .dIn(mid1_img_Y),
    .dInValid(mid1_frame_href),
    .nextDin(nextDin),
	.start(start_pulse),

    // Output interface
    .dOut(scaled_img_Y),
    .dOutValid(scaled_frame_href),
    .nextDout(nextDout),

    // Control signals
    .inputDiscardCnt(8'd0),
    .inputXRes(INPUT_WIDTH - 1),
    .inputYRes(INPUT_HEIGHT - 1),
    .outputXRes(OUTPUT_WIDTH - 1),
    .outputYRes(OUTPUT_HEIGHT - 1),
    .xScale(18'h04000), // 1.6 in Q4.14 format
    .yScale(18'h04000), // 1.6 in Q4.14 format
    .leftOffset(0),
    .topFracOffset(0),
    .nearestNeighbor(1'b0)
);

// Assign the output signals
assign post_frame_vsync = scaled_frame_vsync;
assign post_frame_href = scaled_frame_href;
assign post_frame_clken = scaled_frame_clken;
assign post_img_Y = scaled_img_Y;
assign post_img_Cb = 0;
assign post_img_Cr = 0;
assign post_img_mode = 0;
assign post_img_bit = scaled_img_Y[0];

// module streamScaler #(
// //---------------------------Parameters----------------------------------------
// parameter	DATA_WIDTH =			8,		//Width of input/output data
// parameter	CHANNELS =				1,		//Number of channels of DATA_WIDTH, for color images
// parameter 	DISCARD_CNT_WIDTH =		8,		//Width of inputDiscardCnt
// parameter	INPUT_X_RES_WIDTH =		11,		//Widths of input/output resolution control signals
// parameter	INPUT_Y_RES_WIDTH =		11,
// parameter	OUTPUT_X_RES_WIDTH =	11,
// parameter	OUTPUT_Y_RES_WIDTH =	11,
// parameter	FRACTION_BITS =			8,		//Number of bits for fractional component of coefficients.

// parameter	SCALE_INT_BITS =		4,		//Width of integer component of scaling factor. The maximum input data width to
// 											//multipliers created will be SCALE_INT_BITS + SCALE_FRAC_BITS. Typically these
// 											//values will sum to 18 to match multipliers available in FPGAs.
// parameter	SCALE_FRAC_BITS =		14,		//Width of fractional component of scaling factor
// parameter	BUFFER_SIZE =			4,		//Depth of RFIFO
// //---------------------Non-user-definable parameters----------------------------
// parameter	COEFF_WIDTH =			FRACTION_BITS + 1,
// parameter	SCALE_BITS =			SCALE_INT_BITS + SCALE_FRAC_BITS,
// parameter	BUFFER_SIZE_WIDTH =		((BUFFER_SIZE+1) <= 2) ? 1 :	//wide enough to hold value BUFFER_SIZE + 1
// 									((BUFFER_SIZE+1) <= 4) ? 2 :
// 									((BUFFER_SIZE+1) <= 8) ? 3 :
// 									((BUFFER_SIZE+1) <= 16) ? 4 :
// 									((BUFFER_SIZE+1) <= 32) ? 5 :
// 									((BUFFER_SIZE+1) <= 64) ? 6 : 7
// )(
// //---------------------------Module IO-----------------------------------------
// //Clock and reset
// input wire							clk,
// input wire							rst,

// //User interface
// //Input
// input wire [DATA_WIDTH*CHANNELS-1:0]dIn,
// input wire							dInValid,
// output wire							nextDin,
// input wire							start,

// //Output
// output reg [DATA_WIDTH*CHANNELS-1:0]
// 									dOut,
// output reg							dOutValid,			//latency of 4 clock cycles after nextDout is asserted
// input wire							nextDout,

// //Control
// input wire [DISCARD_CNT_WIDTH-1:0]	inputDiscardCnt,	//Number of input pixels to discard before processing data. Used for clipping
// input wire [INPUT_X_RES_WIDTH-1:0]	inputXRes,			//Resolution of input data minus 1
// input wire [INPUT_Y_RES_WIDTH-1:0]	inputYRes,
// input wire [OUTPUT_X_RES_WIDTH-1:0]	outputXRes,			//Resolution of output data minus 1
// input wire [OUTPUT_Y_RES_WIDTH-1:0]	outputYRes,
// input wire [SCALE_BITS-1:0]			xScale,				//Scaling factors. Input resolution scaled up by 1/xScale. Format Q SCALE_INT_BITS.SCALE_FRAC_BITS
// input wire [SCALE_BITS-1:0]			yScale,				//Scaling factors. Input resolution scaled up by 1/yScale. Format Q SCALE_INT_BITS.SCALE_FRAC_BITS

// input wire [OUTPUT_X_RES_WIDTH-1+SCALE_FRAC_BITS:0]
// 									leftOffset,			//Integer/fraction of input pixel to offset output data horizontally right. Format Q OUTPUT_X_RES_WIDTH.SCALE_FRAC_BITS
// input wire [SCALE_FRAC_BITS-1:0]	topFracOffset,		//Fraction of input pixel to offset data vertically down. Format Q0.SCALE_FRAC_BITS
// input wire							nearestNeighbor		//Use nearest neighbor resize instead of bilinear
// );


endmodule
