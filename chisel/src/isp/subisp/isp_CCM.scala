package subisp

import chisel3._
import chisel3.util._
import Modules._

class CCM(BITS: Int = 8, DELAY_NUM: Int = 5) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
        val I_m_rr = Input(SInt(BITS.W))
        val I_m_rg = Input(SInt(BITS.W))
        val I_m_rb = Input(SInt(BITS.W))
        val I_m_gr = Input(SInt(BITS.W))
        val I_m_gg = Input(SInt(BITS.W))
        val I_m_gb = Input(SInt(BITS.W))
        val I_m_br = Input(SInt(BITS.W))
        val I_m_bg = Input(SInt(BITS.W))
        val I_m_bb = Input(SInt(BITS.W))
    })

    io.post_isp_bus := DontCare
    // input rgb888
    val per_img_red = io.per_isp_bus.img_red
    val per_img_green = io.per_isp_bus.img_green
    val per_img_blue = io.per_isp_bus.img_blue

    val per_frame_href = io.per_isp_bus.frame_href
    val per_frame_vsync = io.per_isp_bus.frame_vsync

    val in_r_1 = RegInit(0.S((BITS + 1).W))
    val in_g_1 = RegInit(0.S((BITS + 1).W))
    val in_b_1 = RegInit(0.S((BITS + 1).W))

    in_r_1 := Cat(0.U(1.W), per_img_red).asSInt
    in_g_1 := Cat(0.U(1.W), per_img_green).asSInt
    in_b_1 := Cat(0.U(1.W), per_img_blue).asSInt

    val data_rr = RegInit(0.S((BITS + 8).W))
    val data_rg = RegInit(0.S((BITS + 8).W))
    val data_rb = RegInit(0.S((BITS + 8).W))
    val data_gr = RegInit(0.S((BITS + 8).W))
    val data_gg = RegInit(0.S((BITS + 8).W))
    val data_gb = RegInit(0.S((BITS + 8).W))
    val data_br = RegInit(0.S((BITS + 8).W))
    val data_bg = RegInit(0.S((BITS + 8).W))
    val data_bb = RegInit(0.S((BITS + 8).W))

    data_rr := io.I_m_rr * in_r_1
    data_rg := io.I_m_rg * in_g_1
    data_rb := io.I_m_rb * in_b_1
    data_gr := io.I_m_gr * in_r_1
    data_gg := io.I_m_gg * in_g_1
    data_gb := io.I_m_gb * in_b_1
    data_br := io.I_m_br * in_r_1
    data_bg := io.I_m_bg * in_g_1
    data_bb := io.I_m_bb * in_b_1

    val data_r = RegInit(0.S((BITS + 8).W))
    val data_g = RegInit(0.S((BITS + 8).W))
    val data_b = RegInit(0.S((BITS + 8).W))

    data_r := (data_rr + data_rg + data_rb) >> 4
    data_g := (data_gr + data_gg + data_gb) >> 4
    data_b := (data_br + data_bg + data_bb) >> 4

    val data_r_1 = RegInit(0.S(BITS.W))
    val data_g_1 = RegInit(0.S(BITS.W))
    val data_b_1 = RegInit(0.S(BITS.W))

    data_r_1 := Mux(
      data_r < 0.S,
      0.S,
      Mux(data_r > 255.S, 255.S, data_r(BITS - 1, 0).asSInt)
    )
    data_g_1 := Mux(
      data_g < 0.S,
      0.S,
      Mux(data_g > 255.S, 255.S, data_g(BITS - 1, 0).asSInt)
    )
    data_b_1 := Mux(
      data_b < 0.S,
      0.S,
      Mux(data_b > 255.S, 255.S, data_b(BITS - 1, 0).asSInt)
    )

    val in_href = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))
    val in_vsync = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))

    in_href.io.din := per_frame_href
    in_vsync.io.din := per_frame_vsync

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.RGB888.id.U
    io.post_isp_bus.img_red := Mux(in_href.io.dout === 1.U, data_r_1, 0.S).asUInt
    io.post_isp_bus.img_green := Mux(in_href.io.dout === 1.U, data_g_1, 0.S).asUInt
    io.post_isp_bus.img_blue := Mux(in_href.io.dout === 1.U, data_b_1, 0.S).asUInt

}
