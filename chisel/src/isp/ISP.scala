import chisel3._
import chisel3.util._

class ISP extends Module {
  val io = IO(new Bundle {
    val per_isp_bus = Flipped(new isp_bus)
    val post_isp_bus = new isp_bus
  })
  
  io.post_isp_bus := DontCare

  val rgb2ycbcr = Module(new Image_RGB888_YCbCr444)
  val SobelDetector = Module(new SobelDetector(1920, 1080, 5))

  rgb2ycbcr.io.per_isp_bus <> io.per_isp_bus

  SobelDetector.io.per_isp_bus <> rgb2ycbcr.io.post_isp_bus

  SobelDetector.io.thresh := 48.U

  io.post_isp_bus <> SobelDetector.io.post_isp_bus

}