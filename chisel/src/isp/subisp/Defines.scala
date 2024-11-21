package subisp

import chisel3._
import chisel3.util._

object ClipAdd {
  def apply(a: UInt, b: SInt): UInt = {
    val sum = a.zext +& b
    val clipped = Mux(sum < 0.S, 0.U, Mux(sum > 255.S, 255.U, sum.asUInt))
    clipped
  }
}

object Clip {
  def apply(a: SInt): UInt = {
    val clipped = Mux(a < 0.S, 0.U, Mux(a > 255.S, 255.U, a.asUInt))
    clipped
  }
}

object ISPMode extends Enumeration {
    val RGB888, Bit, Gray, YCbCr422, RAW_BGGR, RAW_GRBG, YCbCr420, YCbCr444, RGB565 = Value
}

object DPCMode extends Enumeration {
    val Mean, Gradient = Value
}
