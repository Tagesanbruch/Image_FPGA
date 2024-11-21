package subisp

import chisel3._
import chisel3.util._
import Modules._

class YCbCr2RGB(BITS: Int = 8, DELAY_NUM: Int = 5) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
    })

    io.post_isp_bus := DontCare

    val per_frame_vsync = RegInit(false.B)
    val per_frame_href = RegInit(false.B)
    val per_img_raw = RegInit(0.U(10.W))

    per_frame_vsync := io.per_isp_bus.frame_vsync
    per_frame_href := io.per_isp_bus.frame_href
    
    val in_href = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))
    val in_vsync = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))

    in_href.io.din := per_frame_href
    in_vsync.io.din := per_frame_vsync
    
//     //--------------------------------------------
// /*********************************************
// 	R = 1.164(Y-16) + 1.596(Cr-128)
// 	G = 1.164(Y-16) - 0.391(Cb-128) - 0.813(Cr-128)
// 	B = 1.164(Y-16) + 2.018(Cb-128)
// 	->
// 	R = 1.164Y + 1.596Cr - 222.912
// 	G = 1.164Y - 0.391Cb - 0.813Cr + 135.488
// 	B = 1.164Y + 2.018Cb - 276.928
// 	->
// 	R << 9 = 596Y				+ 	817Cr	-	114131
// 	G << 9 = 596Y	-	200Cb	-	416Cr	+	69370
// 	B << 9 = 596Y	+	1033Cb				-	141787

    val img_Y_r1 = RegInit(0.U(20.W))
    val img_Cb_r1 = RegInit(0.U(20.W))
    val img_Cb_r2 = RegInit(0.U(20.W))
    val img_Cr_r1 = RegInit(0.U(20.W))
    val img_Cr_r2 = RegInit(0.U(20.W))

    img_Y_r1 := io.per_isp_bus.img_Y * 596.U
    img_Cb_r1 := io.per_isp_bus.img_Cb * 200.U
    img_Cb_r2 := io.per_isp_bus.img_Cb * 1033.U
    img_Cr_r1 := io.per_isp_bus.img_Cr * 817.U
    img_Cr_r2 := io.per_isp_bus.img_Cr * 416.U

// //--------------------------------------------
// /**********************************************
// 	R << 9 = 596Y				+ 	817Cr	-	114131
// 	G << 9 = 596Y	-	200Cb	-	416Cr	+	69370
// 	B << 9 = 596Y	+	1033Cb				-	141787
// **********************************************/
// reg	[19:0]	XOUT; 	
// reg	[19:0]	YOUT; 
// reg	[19:0]	ZOUT;

    val x_out = RegInit(0.U(20.W))
    val y_out = RegInit(0.U(20.W))
    val z_out = RegInit(0.U(20.W))

    x_out := (img_Y_r1 + img_Cr_r1 - 114131.U) >> 9
    y_out := (img_Y_r1 - img_Cb_r1 - img_Cr_r2 + 69370.U) >> 9
    z_out := (img_Y_r1 + img_Cb_r2 - 141787.U) >> 9

// //------------------------------------------
// //Divide 512 and get the result
// //{xx[19:11], xx[10:0]}
// reg	[7:0]	R, G, B;

    val R = RegInit(0.U(8.W))
    val G = RegInit(0.U(8.W))
    val B = RegInit(0.U(8.W))

    R := Mux(x_out(10), 0.U, Mux(x_out(9, 0) > 255.U, 255.U, x_out(7, 0)))
    G := Mux(y_out(10), 0.U, Mux(y_out(9, 0) > 255.U, 255.U, y_out(7, 0)))
    B := Mux(z_out(10), 0.U, Mux(z_out(9, 0) > 255.U, 255.U, z_out(7, 0)))

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.RGB888.id.U
    io.post_isp_bus.img_red := R
    io.post_isp_bus.img_green := G
    io.post_isp_bus.img_blue := B
}
