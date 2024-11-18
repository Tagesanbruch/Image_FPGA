import chisel3._
import chisel3.util._

class Image_RGB888_YCbCr444 extends Module {
  val io = IO(new Bundle {
    val per_isp_bus = Flipped(new isp_bus)
    val post_isp_bus = new isp_bus
  })
  
  val per_img_red = io.per_isp_bus.img_red
  val per_img_green = io.per_isp_bus.img_green
  val per_img_blue = io.per_isp_bus.img_blue

  val per_frame_clken = io.per_isp_bus.frame_clken
  val per_frame_href = io.per_isp_bus.frame_href
  val per_frame_vsync = io.per_isp_bus.frame_vsync

  val img_red_r0    = RegNext(per_img_red * 77.U, 0.U)
  val img_red_r1    = RegNext(per_img_red * 43.U, 0.U)
  val img_red_r2    = RegNext(per_img_red * 128.U, 0.U)
  val img_green_r0  = RegNext(per_img_green * 150.U, 0.U)
  val img_green_r1  = RegNext(per_img_green * 85.U, 0.U)
  val img_green_r2  = RegNext(per_img_green * 107.U, 0.U)
  val img_blue_r0   = RegNext(per_img_blue * 29.U, 0.U)
  val img_blue_r1   = RegNext(per_img_blue * 128.U, 0.U)
  val img_blue_r2   = RegNext(per_img_blue * 21.U, 0.U)

  val img_Y_r0  = RegNext(img_red_r0 + img_green_r0 + img_blue_r0, 0.U)
  val img_Cb_r0 = RegNext(img_blue_r1 - img_red_r1 - img_green_r1 + 32768.U, 0.U)
  val img_Cr_r0 = RegNext(img_red_r2 + img_green_r2 + img_blue_r2 + 32768.U, 0.U)

  val img_Y_r1 =  RegNext(img_Y_r0(15, 8), 0.U)
  val img_Cb_r1 = RegNext(img_Cb_r0(15, 8), 0.U)
  val img_Cr_r1 = RegNext(img_Cr_r0(15, 8), 0.U)

  // val per_frame_vsync_r = RegNext(RegNext(RegNext(per_frame_vsync, false.B), false.B), false.B)
  // val per_frame_href_r = RegNext(RegNext(RegNext(per_frame_href, false.B), false.B), false.B)
  // val per_frame_clken_r = RegNext(RegNext(RegNext(per_frame_clken, false.B), false.B), false.B)

  val vsync_ShiftReg  = Module(new ShiftReg(3, ShiftRegDirection.Left))
  val href_ShiftReg   = Module(new ShiftReg(3, ShiftRegDirection.Left))
  val clken_ShiftReg  = Module(new ShiftReg(3, ShiftRegDirection.Left))

  vsync_ShiftReg.io.din := per_frame_vsync
  href_ShiftReg.io.din := per_frame_href
  clken_ShiftReg.io.din := per_frame_clken


  io.post_isp_bus.frame_vsync := vsync_ShiftReg.io.dout
  io.post_isp_bus.frame_href  := href_ShiftReg.io.dout
  io.post_isp_bus.frame_clken := clken_ShiftReg.io.dout
  io.post_isp_bus.frame_mode := ISPMode.YCbCr444.id.U
  io.post_isp_bus.img_red := 0.U
  io.post_isp_bus.img_green := 0.U
  io.post_isp_bus.img_blue := 0.U
  io.post_isp_bus.img_Y   := Mux(io.post_isp_bus.frame_href, img_Y_r1, 0.U)
  io.post_isp_bus.img_Cb  := Mux(io.post_isp_bus.frame_href, img_Cb_r1, 0.U)
  io.post_isp_bus.img_Cr  := Mux(io.post_isp_bus.frame_href, img_Cr_r1, 0.U)
}