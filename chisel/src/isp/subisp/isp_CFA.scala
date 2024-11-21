package subisp

import chisel3._
import chisel3.util._
import Modules._

class CFA(BITS: Int = 8, DELAY_NUM: Int = 5, IMG_HDISP: Int, IMG_VDISP: Int) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
    })

    //assuming bggr
    io.post_isp_bus := DontCare
    val hcnt = RegInit(0.U(12.W))
    val vcnt = RegInit(0.U(12.W))

    val per_frame_vsync = RegInit(false.B)
    val per_frame_href = RegInit(false.B)
    val per_img_raw = RegInit(0.U(8.W))

    per_frame_vsync := io.per_isp_bus.frame_vsync
    per_frame_href := io.per_isp_bus.frame_href
    per_img_raw := io.per_isp_bus.img_raw

    
    val in_href = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))
    val in_vsync = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))

    in_href.io.din := per_frame_href
    in_vsync.io.din := per_frame_vsync

    
    val per_frame_href_dly = RegNext(in_href.io.dout, false.B)
    val img_href_neg = dontTouch(WireInit(0.U))
    img_href_neg := !in_href.io.dout && (per_frame_href_dly === 1.U)

    hcnt := Mux(in_href.io.dout === 1.U, hcnt + 1.U, 0.U)
    vcnt := Mux(
      per_frame_vsync === 0.U,
      0.U,
      Mux(img_href_neg === 1.U, vcnt + 1.U, vcnt)
    )

    val matrix = Module(
      new ShiftMatrix(3, IMG_HDISP, 8, IMG_HDISP, IMG_VDISP, DELAY_NUM)
    )

    matrix.io.per_img_vsync := per_frame_vsync
    matrix.io.per_img_href := per_frame_href
    matrix.io.per_img_gray := per_img_raw

    val oddline = Wire(Bool())
    val oddcol = Wire(Bool())
    oddline := vcnt(0)
    oddcol := hcnt(0)

    val rgb_r = RegInit(0.U(10.W))
    val rgb_g = RegInit(0.U(10.W))
    val rgb_b = RegInit(0.U(10.W))

    rgb_r := MuxCase(0.U, Seq(
        (oddline && oddcol)     -> ((matrix.io.matrix(0)(1) + matrix.io.matrix(2)(1)) >> 1),
        (oddline && !oddcol)    -> ((matrix.io.matrix(0)(0) + matrix.io.matrix(0)(2) + matrix.io.matrix(2)(0) + matrix.io.matrix(2)(2)) >> 2),
        (!oddline && oddcol)    -> (matrix.io.matrix(1)(1)),
        (!oddline && !oddcol)   -> ((matrix.io.matrix(1)(0) + matrix.io.matrix(1)(2)) >> 1),
    ))

    rgb_b := MuxCase(0.U, Seq(
        (oddline && oddcol)     -> ((matrix.io.matrix(1)(0) + matrix.io.matrix(1)(2)) >> 1),
        (oddline && !oddcol)    -> matrix.io.matrix(1)(1),
        (!oddline && oddcol)    -> ((matrix.io.matrix(0)(0) + matrix.io.matrix(0)(2) + matrix.io.matrix(2)(0) + matrix.io.matrix(2)(2)) >> 2),
        (!oddline && !oddcol)   -> ((matrix.io.matrix(0)(1) + matrix.io.matrix(2)(1)) >> 1),
    ))

    rgb_g := MuxCase(0.U, Seq(
        (oddline && !oddcol) -> ((matrix.io.matrix(0)(1) + matrix.io.matrix(2)(1) + matrix.io.matrix(1)(0) + matrix.io.matrix(1)(2)) >> 2),
        (oddline && oddcol) -> matrix.io.matrix(1)(1),
        (!oddline && !oddcol) -> matrix.io.matrix(1)(1),
        (!oddline && oddcol) -> ((matrix.io.matrix(0)(1) + matrix.io.matrix(2)(1) + matrix.io.matrix(1)(0) + matrix.io.matrix(1)(2)) >> 2)
    ))


    io.post_isp_bus.frame_mode := ISPMode.RGB888.id.U
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.img_red := rgb_r
    io.post_isp_bus.img_green := rgb_g
    io.post_isp_bus.img_blue := rgb_b

}