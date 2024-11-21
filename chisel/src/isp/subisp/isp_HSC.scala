package subisp

import chisel3._
import chisel3.util._
import Modules._
import LUT._

class HSC(BITS: Int = 8, DELAY_NUM: Int = 5) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
        val I_Hue = Input(UInt(8.W))
        val I_Saturation = Input(UInt(8.W))
    })

    io.post_isp_bus := DontCare

    val per_frame_vsync = RegInit(false.B)
    val per_frame_href = RegInit(false.B)
    val per_img_raw = RegInit(0.U(10.W))

    per_frame_vsync := io.per_isp_bus.frame_vsync
    per_frame_href := io.per_isp_bus.frame_href
    per_img_raw := io.per_isp_bus.img_raw
    
    val in_href = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))
    val in_vsync = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))

    in_href.io.din := per_frame_href
    in_vsync.io.din := per_frame_vsync
    
    val LUT = Module(new LUT_HSC())
    LUT.io.I_mode_0 := HSC_MODE.HSC_COS.id.U
    LUT.io.I_mode_1 := HSC_MODE.HSC_SIN.id.U
    LUT.io.I_data_0 := io.I_Hue
    LUT.io.I_data_1 := io.I_Hue
    
    val cos_data = WireInit(0.U(8.W))
    val sin_data = WireInit(0.U(8.W))

    cos_data := LUT.io.O_data_0
    sin_data := LUT.io.O_data_1

    val Cb_post = WireInit(0.U(16.W))
    val Cr_post = WireInit(0.U(16.W))

    Cb_post := io.per_isp_bus.img_Cb
    Cr_post := io.per_isp_bus.img_Cr

    Cb_post := (cos_data * (io.per_isp_bus.img_Cb - 128.U) + sin_data * (io.per_isp_bus.img_Cr - 128.U)) >> 8
    Cr_post := (cos_data * (io.per_isp_bus.img_Cr - 128.U) - sin_data * (io.per_isp_bus.img_Cb - 128.U)) >> 8

    val Cb_mul = WireInit(0.U(16.W))
    val Cr_mul = WireInit(0.U(16.W))

    Cb_mul := Cb_post * io.I_Saturation
    Cr_mul := Cr_post * io.I_Saturation

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.YCbCr444.id.U

    io.post_isp_bus.img_Y := io.per_isp_bus.img_Y
    io.post_isp_bus.img_Cb := (Cb_mul >> 8)
    io.post_isp_bus.img_Cr := (Cr_mul >> 8)
}
