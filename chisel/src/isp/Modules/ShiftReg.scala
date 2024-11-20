package Modules

import chisel3._
import chisel3.util._

object ShiftRegDirection {
    val Left = false.B
    val Right = true.B
}

class ShiftReg(val Depth: Int, val Direction: Bool) extends Module {
    val io = IO(new Bundle {
        val din = Input(UInt(1.W))
        val dout = Output(UInt(1.W))
    })

    val shiftReg = RegInit(0.U(Depth.W))

    shiftReg := Mux(Direction, Cat(io.din, shiftReg(Depth - 1, 1)), Cat(shiftReg(Depth - 2, 0), io.din))

    io.dout := shiftReg(Depth - 1)
}
