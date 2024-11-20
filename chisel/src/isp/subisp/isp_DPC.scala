package subisp

import chisel3._
import chisel3.util._
import chisel3.SpecifiedDirection.Flip
import Modules._

class DPC(BITS: Int = 8, DELAY_NUM: Int = 5, IMG_HDISP: Int = 1920, IMG_VDISP: Int = 1080) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
        val I_thresh = Input(SInt(8.W))
        val I_mode = Input(UInt(8.W))
        val I_clip = Input(UInt(8.W))
    })

    io.post_isp_bus := DontCare
    val matrix = Module(
      new ShiftMatrix(5, IMG_HDISP, 8, IMG_HDISP, IMG_VDISP, DELAY_NUM)
    )

    val per_img_vsync = RegInit(false.B)
    val per_img_href = RegInit(false.B)
    val per_img_gray = RegInit(0.U(8.W))

    val pixel_array = Wire(Vec(3, Vec(3, UInt(8.W))))

    // pixel_arr
    // pixel_array := matrix.io.matrix
    // change 5*5 to 3*3
    for(i <- 0 until 3) {
        for(j <- 0 until 3) {
            pixel_array(i)(j) := matrix.io.matrix(i * 2)(j * 2)
        }
    }

    val dp_arr_abs = Wire(Vec(8, SInt(9.W)))
    val detected_dead = WireInit(false.B)
    for(i <- 0 until 8) {
        dp_arr_abs(i) := (pixel_array(1)(1).asSInt - pixel_array(i/3)(i%3).asSInt).abs.asUInt
        detected_dead := detected_dead | (dp_arr_abs(i) > io.I_thresh)
    }

    val mean_center = Wire(UInt(8.W))
    mean_center := (pixel_array(0)(1) + pixel_array(1)(0) + pixel_array(1)(2) + pixel_array(2)(1)) >> 2
    
    val gradient_center = Wire(UInt(8.W))
    val dv = Wire(SInt(9.W))
    val dh = Wire(SInt(9.W))
    val ddl = Wire(SInt(9.W))
    val ddr = Wire(SInt(9.W))

    dv := ((pixel_array(1)(1) << 1) - pixel_array(0)(1) - pixel_array(2)(1)).abs
    dh := ((pixel_array(1)(1) << 1) - pixel_array(1)(0) - pixel_array(1)(2)).abs
    ddl := ((pixel_array(1)(1) << 1) - pixel_array(0)(0) - pixel_array(2)(2)).abs
    ddr := ((pixel_array(1)(1) << 1) - pixel_array(0)(2) - pixel_array(2)(0)).abs

    val min_dist = Wire(SInt(9.W))

    min_dist := MuxCase(dv, Seq(
        (dv <= dh && dv <= ddl && dv <= ddr) -> dv,
        (dh <= dv && dh <= ddl && dh <= ddr) -> dh,
        (ddl <= dv && ddl <= dh && ddl <= ddr) -> ddl,
        (ddr <= dv && ddr <= dh && ddr <= ddl) -> ddr
    ))

    gradient_center := MuxCase(pixel_array(1)(1), Seq(
        (min_dist === dv) -> ((pixel_array(0)(1) + pixel_array(2)(1) + 1.U) >> 1),
        (min_dist === dh) -> ((pixel_array(1)(0) + pixel_array(1)(2) + 1.U) >> 1),
        (min_dist === ddl) -> ((pixel_array(0)(0) + pixel_array(2)(2) + 1.U) >> 1),
        (min_dist === ddr) -> ((pixel_array(0)(2) + pixel_array(2)(0) + 1.U) >> 1)
    ))

    // TODO: output......

}