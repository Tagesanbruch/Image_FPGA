package subisp

import chisel3._
import chisel3.util._
import Modules._

class Line_Shift_RAM_8Bit extends BlackBox with HasBlackBoxInline{
    val io = IO(new Bundle{
        val clk = Input(Clock())
        val rst_n = Input(Bool())
        val clken = Input(Bool())
        val din = Input(UInt(8.W))
        val dout = Output(UInt(8.W))
    })
    setInline("Line_Shift_RAM_8Bit.v",
    """
    |//  by CrazyBird
    |        module Line_Shift_RAM_8Bit
    |#(
    |    parameter DATA_WIDTH = 8    ,
    |    parameter ADDR_WIDTH = 11   ,
    |    parameter DATA_DEPTH = 1280 ,
    |    parameter DELAY_NUM  = 0
    |)(
    |    input  wire                     clk     ,
    |    input  wire                     rst_n   ,
    |    input  wire                     clken   ,
    |    input  wire [DATA_WIDTH-1:0]    din     ,   
    |    output wire [DATA_WIDTH-1:0]    dout    
    |);
    |//----------------------------------------------------------------------
    |localparam BRAM_DEPTH = DATA_DEPTH + 1;
    |localparam INIT_ADDR  = DATA_DEPTH - DELAY_NUM;
    |//----------------------------------------------------------------------
    |reg             [ADDR_WIDTH-1:0]    bram_waddr;
    |reg             [ADDR_WIDTH-1:0]    bram_raddr;
    |always @(posedge clk or negedge rst_n)
    |begin
    |    if(rst_n == 1'b0)
    |    begin
    |        bram_waddr <= INIT_ADDR;
    |        bram_raddr <= 0;
    |    end
    |    else
    |    begin
    |        if(clken == 1'b1)
    |        begin
    |            if(bram_waddr < DATA_DEPTH)
    |                bram_waddr <= bram_waddr + 1'b1;
    |            else
    |                bram_waddr <= 0;
    |            if(bram_raddr < DATA_DEPTH)
    |                bram_raddr <= bram_raddr + 1'b1;
    |            else
    |                bram_raddr <= 0;
    |        end
    |        else
    |        begin
    |            bram_waddr <= bram_waddr;
    |            bram_raddr <= bram_raddr;
    |        end
    |    end
    |end
    |//----------------------------------------------------------------------
    |wire            [DATA_WIDTH-1:0]    bram_wdata;
    |wire                                bram_wenb;
    |reg            [DATA_WIDTH-1:0]    bram_rdata;
    |assign bram_wdata = din;
    |assign bram_wenb  = clken;
    |localparam ADDR_MSB = 2 ** ADDR_WIDTH - 1; 
    |reg 	[DATA_WIDTH-1:0] 	r_ram[ADDR_MSB:0]; 
    |always @(posedge clk) begin
    |    if(bram_wenb) begin
    |        r_ram[bram_waddr] <= bram_wdata; 
    |    end else begin
    |    end
    |end
    |always @(posedge clk) begin
    |    bram_rdata <= r_ram[bram_raddr]; 
    |end
    |//shift_reg_bram
    |//#(
    |//    .DATA_WIDTH_A   (DATA_WIDTH ),
    |//    .ADDR_WIDTH_A   (ADDR_WIDTH ),
    |//    .DATA_DEPTH_A   (BRAM_DEPTH ),
    |//    .DATA_WIDTH_B   (DATA_WIDTH ),
    |//    .ADDR_WIDTH_B   (ADDR_WIDTH ),
    |//    .DATA_DEPTH_B   (BRAM_DEPTH )
    |//)
    |//u_shift_reg_bram
    |//( 
    |//    .clka   (clk        ),
    |//    .addra  (bram_waddr ),
    |//    .dia    (bram_wdata ),
    |//    .cea    (bram_wenb  ),
    |//    
    |//    .clkb   (clk        ),
    |//    .addrb  (bram_raddr ),
    |//    .ceb    (1'b1       ),
    |//    .dob    (bram_rdata )
    |//);
    |assign dout = bram_rdata;
    |endmodule

    """.stripMargin
    )
}

class CFA_verilog extends BlackBox with HasBlackBoxInline{
    val io = IO(new Bundle {
        val I_clk = Input(Clock())
        val I_rst_n = Input(Bool())
        val I_raw_vs = Input(Bool())
        val I_raw_de = Input(Bool())
        val I_raw_data = Input(UInt(8.W))
        val O_rgb_vs = Output(Bool())
        val O_rgb_de = Output(Bool())
        val O_rgb_r = Output(UInt(8.W))
        val O_rgb_g = Output(UInt(8.W))
        val O_rgb_b = Output(UInt(8.W))
    })
    setInline("CFA_verilog.v",
    """
        |// by anlogic
        |module CFA_verilog#(
        |    parameter   [13:0]  H_SIZE = 1920,    //640*480
        |    parameter   [13:0]  V_SIZE = 1080
        |    )
        |    (
        |        //global clock
        |    input   wire        I_clk,            //cmos video pixel clock
        |    input   wire        I_rst_n,          //global reset
        |    input   wire        I_raw_vs,          //Prepared Image data vsync valid signal
        |    input   wire        I_raw_de,          //Prepared Image data href vaild  signal
        |    input   wire[7:0]   I_raw_data,        //Prepared Image data 8 Bit RAW Data
        |    output  wire        O_rgb_vs,   //Processed Image data vsync valid signal
        |    output  wire        O_rgb_de,    //Processed Image data href vaild  signal
        |    output  wire[7:0]   O_rgb_r,       //Prepared Image green data to be processed 
        |    output  wire[7:0]   O_rgb_g,     //Prepared Image green data to be processed
        |    output  wire[7:0]   O_rgb_b       //Prepared Image blue data to be processed
        |    );
        |    wire [7:0]raw_v_1_d0;//frame data of the 1th row
        |    wire [7:0]raw_v_2_d0;//frame data of the 2th row
        |    reg  [7:0]raw_v_3_d0;//frame data of the 3th row
        |    reg  [7:0]raw_v_1_d1,raw_v_1_d2;
        |    reg  [7:0]raw_v_2_d1,raw_v_2_d2;
        |    reg  [7:0]raw_v_3_d1,raw_v_3_d2;
        |    reg  raw_de_d1,raw_de_d2,raw_de_d3,raw_de_d4;  
        |    reg  raw_vs_d1,raw_vs_d2,raw_vs_d3,raw_vs_d4;
        |    wire  shift_clk_en = I_raw_de;
        |    Line_Shift_RAM_8Bit#(
        |    .DATA_WIDTH (8          ),
        |    .ADDR_WIDTH (11         ),
        |    .DATA_DEPTH (H_SIZE     ),
        |    .DELAY_NUM  (0          )
        |    )
        |    u0_Line_Shift_RAM_8Bit
        |    (
        |    .clk    (I_clk          ),
        |    .rst_n  (I_rst_n        ),
        |    .clken  (shift_clk_en   ),
        |    .din    (I_raw_data     ),   
        |    .dout   (raw_v_2_d0     )
        |    );
        |    Line_Shift_RAM_8Bit#(
        |    .DATA_WIDTH (8          ),
        |    .ADDR_WIDTH (11         ),
        |    .DATA_DEPTH (H_SIZE     ),
        |    .DELAY_NUM  (1          )
        |    )
        |    u1_Line_Shift_RAM_8Bit
        |    (
        |    .clk    (I_clk          ),
        |    .rst_n  (I_rst_n        ),
        |    .clken  (shift_clk_en   ),
        |    .din    (raw_v_2_d0     ),   
        |    .dout   (raw_v_1_d0     )
        |    );
        |    always@(posedge I_clk)begin
        |        raw_v_3_d0 <= I_raw_de ? I_raw_data : raw_v_3_d0; 
        |        raw_v_3_d1 <= raw_v_3_d0;
        |        raw_v_3_d2 <= raw_v_3_d1;
        |        raw_v_1_d1 <= raw_v_1_d0;
        |        raw_v_1_d2 <= raw_v_1_d1;
        |        raw_v_2_d1 <= raw_v_2_d0;
        |        raw_v_2_d2 <= raw_v_2_d1;
        |        raw_de_d1  <= I_raw_de;
        |        raw_de_d2  <= raw_de_d1;
        |        raw_de_d3  <= raw_de_d2;
        |        raw_de_d4  <= raw_de_d3;
        |        raw_vs_d1  <= I_raw_vs;
        |        raw_vs_d2  <= raw_vs_d1;
        |        raw_vs_d3  <= raw_vs_d2;
        |        raw_vs_d4  <= raw_vs_d3;
        |    end
        |    reg  [7:0]raw_v_h_11,raw_v_h_12,raw_v_h_13;
        |    reg  [7:0]raw_v_h_21,raw_v_h_22,raw_v_h_23;
        |    reg  [7:0]raw_v_h_31,raw_v_h_32,raw_v_h_33;
        |    reg     [13:0]  pixel_cnt;
        |    always@(posedge I_clk)begin
        |    if(raw_de_d1|raw_de_d2)begin 
        |        pixel_cnt <=  (pixel_cnt < H_SIZE) ? pixel_cnt + 1'b1 : 10'd0;   //Point Counter  
        |        if(pixel_cnt == 0)begin
        |            {raw_v_h_11,raw_v_h_12,raw_v_h_13} <= {8'd0,8'd0,8'd0};
        |            {raw_v_h_21,raw_v_h_22,raw_v_h_23} <= {8'd0,8'd0,8'd0};
        |            {raw_v_h_31,raw_v_h_32,raw_v_h_33} <= {8'd0,8'd0,8'd0};
        |        end  
        |        else if(pixel_cnt == 1)begin
        |            {raw_v_h_11,raw_v_h_12,raw_v_h_13} <= {raw_v_1_d0,raw_v_1_d1,raw_v_1_d0};
        |            {raw_v_h_21,raw_v_h_22,raw_v_h_23} <= {raw_v_2_d0,raw_v_2_d1,raw_v_2_d0};
        |            {raw_v_h_31,raw_v_h_32,raw_v_h_33} <= {raw_v_3_d0,raw_v_3_d1,raw_v_3_d0};    
        |        end
        |        else if(pixel_cnt == H_SIZE)begin
        |            {raw_v_h_11,raw_v_h_12,raw_v_h_13} <= {raw_v_1_d2,raw_v_1_d0,raw_v_1_d2};
        |            {raw_v_h_21,raw_v_h_22,raw_v_h_23} <= {raw_v_2_d2,raw_v_1_d0,raw_v_1_d2};
        |            {raw_v_h_31,raw_v_h_32,raw_v_h_33} <= {raw_v_3_d2,raw_v_1_d0,raw_v_1_d2};   
        |        end
        |        else begin
        |            {raw_v_h_11,raw_v_h_12,raw_v_h_13} <= {raw_v_1_d2,raw_v_1_d1,raw_v_1_d0};
        |            {raw_v_h_21,raw_v_h_22,raw_v_h_23} <= {raw_v_2_d2,raw_v_2_d1,raw_v_2_d0};
        |            {raw_v_h_31,raw_v_h_32,raw_v_h_33} <= {raw_v_3_d2,raw_v_3_d1,raw_v_3_d0};   
        |        end
        |    end
        |    else begin
        |            pixel_cnt <= 0;
        |            {raw_v_h_11,raw_v_h_12,raw_v_h_13} <= {8'd0,8'd0,8'd0};
        |            {raw_v_h_21,raw_v_h_22,raw_v_h_23} <= {8'd0,8'd0,8'd0};
        |            {raw_v_h_31,raw_v_h_32,raw_v_h_33} <= {8'd0,8'd0,8'd0};
        |    end
        |    end
        |    //----------------------------------------
        |    //Count the frame lines
        |    reg [10:0]  v_cnt;
        |    reg [13:0]  h_cnt;
        |    always@(posedge I_clk or negedge I_rst_n)begin
        |        if(!I_rst_n || I_raw_vs == 0)
        |            v_cnt <= 0;
        |        else if((raw_de_d3 == 1) && (raw_de_d2 == 0)) //Frame valid
        |            v_cnt <= (v_cnt < V_SIZE - 1'b1) ? v_cnt + 1'b1 : 11'd0;
        |        else 
        |            v_cnt <= v_cnt;
        |    end
        |    always@(posedge I_clk or negedge I_rst_n)begin
        |        if(!I_rst_n )
        |            h_cnt <= 0;
        |        else if(raw_de_d3 == 1'b1)
        |            h_cnt <= (h_cnt < H_SIZE - 1'b1) ? h_cnt + 1'b1 : 11'd0;
        |        else
        |            h_cnt <= 0;
        |    end
        |    //Convet RAW 2 RGB888 Format
        |    reg [9:0]   rgb_r,rgb_g,rgb_b;
        |    always@(posedge I_clk or negedge I_rst_n)begin
        |        if(!I_rst_n)begin
        |            rgb_r  <=  0;
        |            rgb_g  <=  0;
        |            rgb_b  <=  0; 
        |        end else begin
        |            case({v_cnt[0], h_cnt[0]})
        |            2'b11: begin  //odd lines + odd point  //Center Green
        |                rgb_r  <=  (raw_v_h_12 + raw_v_h_32)>>1;
        |                rgb_g  <=   raw_v_h_22;     
        |                rgb_b  <=  (raw_v_h_21 + raw_v_h_23)>>1;
        |            end
        |            2'b10: begin  //odd lines + even point //Center Blue
        |                rgb_r  <=  (raw_v_h_11 + raw_v_h_13 + raw_v_h_31 + raw_v_h_33)>>2;
        |                rgb_g  <=  (raw_v_h_12 + raw_v_h_21 + raw_v_h_23 + raw_v_h_32)>>2;
        |                rgb_b  <=   raw_v_h_22;
        |            end
        |            2'b01: begin  //even lines + odd point //Center Red
        |                rgb_r  <=   raw_v_h_22;
        |                rgb_g  <=  (raw_v_h_12 + raw_v_h_21 + raw_v_h_23 + raw_v_h_32)>>2;
        |                rgb_b  <=  (raw_v_h_11 + raw_v_h_13 + raw_v_h_31 + raw_v_h_33)>>2;
        |            end
        |            2'b00: begin//even lines + even point  //Center Green        
        |                rgb_r  <=  (raw_v_h_21 + raw_v_h_23)>>1;
        |                rgb_g  <=   raw_v_h_22;
        |                rgb_b  <=  (raw_v_h_12 + raw_v_h_32)>>1;
        |            end 
        |            endcase
        |        end
        |    end
        |    assign O_rgb_r    = rgb_r[7:0];
        |    assign O_rgb_g    = rgb_g[7:0];
        |    assign O_rgb_b    = rgb_b[7:0];
        |    assign O_rgb_de   = raw_de_d4;
        |    assign O_rgb_vs   = raw_vs_d4;
        |endmodule
    """.stripMargin
    )
}


class CFA_Verilog_TOP(BITS: Int = 8, DELAY_NUM: Int = 5, IMG_HDISP: Int, IMG_VDISP: Int) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
    })

    io.post_isp_bus := DontCare
    val CFA_verilog = Module(new CFA_verilog)
    
    // val Line_Shift_RAM_8Bit = Module(new Line_Shift_RAM_8Bit)
    CFA_verilog.io.I_clk := clock
    CFA_verilog.io.I_rst_n := ~reset.asBool
    CFA_verilog.io.I_raw_vs := io.per_isp_bus.frame_vsync
    CFA_verilog.io.I_raw_de := io.per_isp_bus.frame_href
    CFA_verilog.io.I_raw_data := io.per_isp_bus.img_raw

    io.post_isp_bus.frame_vsync := CFA_verilog.io.O_rgb_vs
    io.post_isp_bus.frame_href := CFA_verilog.io.O_rgb_de
    io.post_isp_bus.img_red := CFA_verilog.io.O_rgb_r
    io.post_isp_bus.img_green := CFA_verilog.io.O_rgb_g
    io.post_isp_bus.img_blue := CFA_verilog.io.O_rgb_b
    io.post_isp_bus.frame_mode := ISPMode.RGB888.id.U

}