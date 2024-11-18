import chisel3._
import chisel3.util._

class Sqrt extends Module {
  val io = IO(new Bundle {
    val sys_clk = Input(Clock())
    val sys_rst = Input(Bool())
    val din = Input(UInt(21.W))
    val din_valid = Input(Bool())
    val dout = Output(UInt(11.W))
    val dout_valid = Output(Bool())
  })

  val data = RegInit(VecInit(Seq.fill(12)(0.U(23.W))))
  val bits = RegInit(VecInit(Seq.fill(12)(0.U(11.W))))
  val valid = RegInit(VecInit(Seq.fill(12)(false.B)))
  val mult = WireInit(VecInit(Seq.fill(12)(0.U(24.W))))

  // Stage 1
  data(0) := Cat(io.din, 0.U(2.W))
  bits(0) := 0.U
  valid(0) := io.din_valid
  mult(0) := "b100000000000".U * "b100000000000".U
  
  // Stages 2 to 12
  for (i <- 1 until 12) {    
    val shift = 11 - i
    val bitMask = (1.U << shift).asUInt
    val shiftedBits = Cat(bits(i-1)(10, shift), bitMask)
    mult(i) := shiftedBits * shiftedBits
    bits(i) := Mux(mult(i) <= data(i-1), bits(i-1) | bitMask, bits(i-1))
    data(i) := data(i-1)
    valid(i) := valid(i-1)
  }

  // Output
  io.dout := bits(11)
  io.dout_valid := valid(11)
}