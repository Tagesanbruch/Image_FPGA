package subisp

import chisel3._
import chisel3.util._

// val per_frame_vsync = Input(Bool())
// val per_frame_href = Input(Bool())
// val per_frame_clken = Input(Bool())
// val per_img_red = Input(UInt(8.W))
// val per_img_green = Input(UInt(8.W))
// val per_img_blue = Input(UInt(8.W))

class isp_bus extends Bundle {
  val frame_vsync  = Output(Bool())
  val frame_href   = Output(Bool())
  val frame_mode   = Output(UInt(8.W))
  val img_raw      = Output(UInt(8.W))
  val img_red      = Output(UInt(8.W))
  val img_green    = Output(UInt(8.W))
  val img_blue     = Output(UInt(8.W))
  val img_Y        = Output(UInt(8.W))
  val img_Cb       = Output(UInt(8.W))
  val img_Cr       = Output(UInt(8.W))
  val img_bit      = Output(Bool())
}
