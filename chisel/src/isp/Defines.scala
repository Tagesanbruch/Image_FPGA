import chisel3._
import chisel3.util._

object ISPMode extends Enumeration {
  val RGB888, YCbCr422, YCbCr420, YCbCr444, RGB565, Bit, gray = Value
}