package Modules

import chisel3._
import chisel3.util._

class ShiftMatrix(
    N: Int,
    FIFO_DEPTH: Int,
    DATA_WIDTH: Int,
    IMG_HDISP: Int,
    IMG_VDISP: Int,
    DELAY_NUM: Int
) extends Module {
    val io = IO(new Bundle {
        val per_img_vsync = Input(Bool())
        val per_img_href = Input(Bool())
        val per_img_gray = Input(UInt(8.W))
        val matrix_img_vsync = Output(Bool())
        val matrix_img_href = Output(Bool())
        val matrix_top_edge_flag = Output(Bool())
        val matrix_bottom_edge_flag = Output(Bool())
        val matrix_left_edge_flag = Output(Bool())
        val matrix_right_edge_flag = Output(Bool())
        val matrix = Output(Vec(N, Vec(N, UInt(DATA_WIDTH.W))))
    })

    val hcnt = RegInit(0.U(12.W))
    val vcnt = RegInit(0.U(12.W))
    val extend_last_row_cnt = RegInit(0.U(12.W))
    val per_img_href_dly = RegNext(io.per_img_href, 0.B)
    val img_href_neg = dontTouch(WireInit(0.U))
    img_href_neg := !io.per_img_href && per_img_href_dly
    val extend_last_row_en = Wire(Bool())

    hcnt := Mux(io.per_img_href, hcnt + 1.U, 0.U)
    vcnt := Mux(
      io.per_img_vsync === 0.U,
      0.U,
      Mux(img_href_neg === 1.U, vcnt + 1.U, vcnt)
    )

    extend_last_row_cnt :=
        Mux(
          io.per_img_href === 1.U && vcnt === (IMG_VDISP - 1).U && hcnt === (IMG_HDISP - 1).U,
          1.U,
          Mux(
            extend_last_row_cnt > 0.U && extend_last_row_cnt < (DELAY_NUM + IMG_HDISP).U,
            extend_last_row_cnt + 1.U,
            0.U
          )
        )
    extend_last_row_en := extend_last_row_cnt > DELAY_NUM.U

    val fifos = Seq.fill(N - 1)(Module(new SyncFIFO(DATA_WIDTH, FIFO_DEPTH)))

    fifos.zipWithIndex.foreach { case (fifo, i) =>
        fifo.io.wr_en := io.per_img_href && (if (i == 0) true.B else vcnt >= i.U)
        fifo.io.din := (if (i == 0) io.per_img_gray else fifos(i - 1).io.dout)
        fifo.io.rd_en := io.per_img_href && (vcnt >= (i + 1).U) || extend_last_row_en
    }

    val MatrixReg = RegInit(VecInit(Seq.fill(N)(VecInit(Seq.fill(N)(0.U(DATA_WIDTH.W))))))
    MatrixReg(0) := RegInit(VecInit(io.per_img_gray +: MatrixReg(0).init))
    for (i <- 1 until N) {
        MatrixReg(i) := RegInit(VecInit(fifos(i - 1).io.dout +: MatrixReg(i).init))
    }

    io.matrix := MatrixReg

    io.matrix_top_edge_flag := vcnt < (N.U - 1.U)
    io.matrix_bottom_edge_flag := vcnt >= ((IMG_VDISP - N).U)
    io.matrix_left_edge_flag := hcnt < (N.U - 1.U)
    io.matrix_right_edge_flag := hcnt >= ((IMG_HDISP - N).U)

    io.matrix_img_vsync := io.per_img_vsync
    io.matrix_img_href := io.per_img_href
}
