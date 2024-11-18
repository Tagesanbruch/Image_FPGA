import chisel3._
import chisel3.util._

class ISP extends Module {
  val io = IO(new Bundle {
    val per_isp_bus = Flipped(new isp_bus)
    val post_isp_bus = new isp_bus
  })
  
  val rgb2ycbcr = Module(new Image_RGB888_YCbCr444)
  rgb2ycbcr.io.per_isp_bus <> io.per_isp_bus
  io.post_isp_bus <> rgb2ycbcr.io.post_isp_bus

}