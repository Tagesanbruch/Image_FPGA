package subisp

import chisel3._
import chisel3.util._
import Modules._

class CSC(BITS: Int = 8, DELAY_NUM: Int = 5) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
    })

    io.post_isp_bus := DontCare

    val per_img_red = io.per_isp_bus.img_red
    val per_img_green = io.per_isp_bus.img_green
    val per_img_blue = io.per_isp_bus.img_blue

    val per_frame_href = io.per_isp_bus.frame_href
    val per_frame_vsync = io.per_isp_bus.frame_vsync

    // 	reg [BITS-1:0] data_r;
	// reg [BITS-1:0] data_g;
	// reg [BITS-1:0] data_b;
	// always @ (posedge pclk or negedge rst_n) begin
	// 	if (!rst_n) begin
	// 		data_r <= 0;
	// 		data_g <= 0;
	// 		data_b <= 0;
	// 	end
	// 	else begin
	// 		data_r <= in_r;
	// 		data_g <= in_g;
	// 		data_b <= in_b;
	// 	end
	// end

	val data_r = RegInit(0.U(BITS.W))
	val data_g = RegInit(0.U(BITS.W))
	val data_b = RegInit(0.U(BITS.W))

	data_r := per_img_red
	data_g := per_img_green
	data_b := per_img_blue

	// reg [BITS-1+8:0] y_r, y_g, y_b;
	// reg [BITS-1+8:0] u_r, u_g, u_b;
	// reg [BITS-1+8:0] v_r, v_g, v_b;

	val y_r = RegInit(0.U((BITS+8).W))
	val y_g = RegInit(0.U((BITS+8).W))
	val y_b = RegInit(0.U((BITS+8).W))
	val u_r = RegInit(0.U((BITS+8).W))
	val u_g = RegInit(0.U((BITS+8).W))
	val u_b = RegInit(0.U((BITS+8).W))
	val v_r = RegInit(0.U((BITS+8).W))
	val v_g = RegInit(0.U((BITS+8).W))
	val v_b = RegInit(0.U((BITS+8).W))

	y_r := data_r * 77.U
	y_g := data_g * 150.U
	y_b := data_b * 29.U
	u_r := data_r * 43.U
	u_g := data_g * 85.U
	u_b := data_b * 128.U
	v_r := data_r * 128.U
	v_g := data_g * 107.U
	v_b := data_b * 21.U

	// always @ (posedge pclk or negedge rst_n) begin
	// 	if (!rst_n) begin
	// 		y_r <= 0;
	// 		y_g <= 0;
	// 		y_b <= 0;
	// 		u_r <= 0;
	// 		u_g <= 0;
	// 		u_b <= 0;
	// 		v_r <= 0;
	// 		v_g <= 0;
	// 		v_b <= 0;
	// 	end
	// 	else begin
	// 		y_r <= data_r * 8'd77;
	// 		y_g <= data_g * 8'd150;
	// 		y_b <= data_b * 8'd29;
	// 		u_r <= data_r * 8'd43;
	// 		u_g <= data_g * 8'd85;
	// 		u_b <= data_b * 8'd128;
	// 		v_r <= data_r * 8'd128;
	// 		v_g <= data_g * 8'd107;
	// 		v_b <= data_b * 8'd21;
	// 	end
	// end

	// reg [BITS-1+8:0] data_y;
	// reg [BITS-1+8:0] data_u;
	// reg [BITS-1+8:0] data_v;

	val data_y = RegInit(0.U((BITS+8).W))
	val data_u = RegInit(0.U((BITS+8).W))
	val data_v = RegInit(0.U((BITS+8).W))

	data_y := y_r + y_g + y_b
	data_u := u_b - u_r - u_g + (1.U << (BITS-1+8)).asUInt
	data_v := v_r - v_g - v_b + (1.U << (BITS-1+8)).asUInt

	// always @ (posedge pclk or negedge rst_n) begin
	// 	if (!rst_n) begin
	// 		data_y <= 0;
	// 		data_u <= 0;
	// 		data_v <= 0;
	// 	end
	// 	else begin
	// 		data_y <= y_r + y_g + y_b;
	// 		data_u <= u_b - u_r - u_g + (1'b1 << (BITS-1+8)); //compatible 10bit RGB
	// 		data_v <= v_r - v_g - v_b + (1'b1 << (BITS-1+8));
	// 	end
	// end

	// localparam DLY_CLK = 3;
	// reg [DLY_CLK-1:0] href_dly;
	// reg [DLY_CLK-1:0] vsync_dly;
	// always @ (posedge pclk or negedge rst_n) begin
	// 	if (!rst_n) begin
	// 		href_dly <= 0;
	// 		vsync_dly <= 0;
	// 	end
	// 	else begin
	// 		href_dly <= {href_dly[DLY_CLK-2:0], in_href};
	// 		vsync_dly <= {vsync_dly[DLY_CLK-2:0], in_vsync};
	// 	end
	// end

    // val img_Y_r0 = RegNext(img_red_r0 + img_green_r0 + img_blue_r0, 0.U)
    // val img_Cb_r0 =
    //     RegNext(img_blue_r1 - img_red_r1 - img_green_r1 + 32768.U, 0.U)
    // val img_Cr_r0 =
    //     RegNext(img_red_r2 + img_green_r2 + img_blue_r2 + 32768.U, 0.U)

    // val per_frame_vsync_r = RegNext(RegNext(RegNext(per_frame_vsync, false.B), false.B), false.B)
    // val per_frame_href_r = RegNext(RegNext(RegNext(per_frame_href, false.B), false.B), false.B)
    // val per_frame_clken_r = RegNext(RegNext(RegNext(per_frame_clken, false.B), false.B), false.B)

    val vsync_ShiftReg = Module(new ShiftReg(3, ShiftRegDirection.Left))
    val href_ShiftReg = Module(new ShiftReg(3, ShiftRegDirection.Left))

    vsync_ShiftReg.io.din := per_frame_vsync
    href_ShiftReg.io.din := per_frame_href

    io.post_isp_bus.frame_vsync := vsync_ShiftReg.io.dout
    io.post_isp_bus.frame_href := href_ShiftReg.io.dout
    io.post_isp_bus.frame_mode := ISPMode.YCbCr422.id.U
    io.post_isp_bus.img_red := 0.U
    io.post_isp_bus.img_green := 0.U
    io.post_isp_bus.img_blue := 0.U
    io.post_isp_bus.img_Y := Mux(io.post_isp_bus.frame_href, data_y(BITS - 1 + 8, 8), 0.U)
    io.post_isp_bus.img_Cb := Mux(io.post_isp_bus.frame_href, data_u(BITS - 1 + 8, 8), 0.U)
    io.post_isp_bus.img_Cr := Mux(io.post_isp_bus.frame_href, data_v(BITS - 1 + 8, 8), 0.U)
}