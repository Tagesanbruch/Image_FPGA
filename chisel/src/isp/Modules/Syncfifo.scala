package Modules

import chisel3._
import chisel3.util._

class SyncFIFO(val width: Int, val depth: Int) extends Module {
  val io = IO(new Bundle {
    val wr_en = Input(Bool())
    val din = Input(UInt(width.W))
    val full = Output(Bool())
    val rd_en = Input(Bool())
    val dout = Output(UInt(width.W))
    val empty = Output(Bool())
    val data_count = Output(UInt(log2Ceil(depth).W))
  })

  val mem = SyncReadMem(depth, UInt(width.W))
  val writePointer = RegInit(0.U(log2Ceil(depth).W))
  val readPointer = RegInit(0.U(log2Ceil(depth).W))
  val dataCount = RegInit(0.U(log2Ceil(depth).W))

  val writeValid = io.wr_en && !io.full
  val readValid = io.rd_en && !io.empty

    when(writeValid) {
      mem.write(writePointer, io.din)
      writePointer := Mux(writePointer === (depth - 1).U, 0.U, writePointer + 1.U)
    }
    when(readValid) {
      readPointer := Mux(readPointer === (depth - 1).U, 0.U, readPointer + 1.U)
    }
    when(writeValid && !readValid) {
      dataCount := dataCount + 1.U
    } .elsewhen(!writeValid && readValid) {
      dataCount := dataCount - 1.U
    }
    
  io.dout := mem.read(readPointer, readValid)
  io.full := dataCount === depth.U
  io.empty := dataCount === 0.U
  io.data_count := dataCount
}
