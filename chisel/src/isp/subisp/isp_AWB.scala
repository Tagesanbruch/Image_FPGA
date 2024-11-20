package subisp

import chisel3._
import chisel3.util._
import Modules._

class AWB(BITS: Int = 8, DELAY_NUM: Int = 5) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
        // val I_m_rr = Input(SInt(BITS.W))
        // val I_m_rg = Input(SInt(BITS.W))
        // val I_m_rb = Input(SInt(BITS.W))
        // val I_m_gr = Input(SInt(BITS.W))
        // val I_m_gg = Input(SInt(BITS.W))
        // val I_m_gb = Input(SInt(BITS.W))
        // val I_m_br = Input(SInt(BITS.W))
        // val I_m_bg = Input(SInt(BITS.W))
        // val I_m_bb = Input(SInt(BITS.W))
    })

    io.post_isp_bus := DontCare

    val per_img_red = io.per_isp_bus.img_red
    val per_img_green = io.per_isp_bus.img_green
    val per_img_blue = io.per_isp_bus.img_blue

    val per_frame_href = io.per_isp_bus.frame_href
    val per_frame_vsync = io.per_isp_bus.frame_vsync

    // TODO: RAW or RGB?

    val in_href = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))
    val in_vsync = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))

    in_href.io.din := per_frame_href
    in_vsync.io.din := per_frame_vsync

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.RGB888.id.U
    // io.post_isp_bus.img_red := Mux(in_href.io.dout === 1.U, data_r_1, 0.S).asUInt
    // io.post_isp_bus.img_green := Mux(in_href.io.dout === 1.U, data_g_1, 0.S).asUInt
    // io.post_isp_bus.img_blue := Mux(in_href.io.dout === 1.U, data_b_1, 0.S).asUInt

}
