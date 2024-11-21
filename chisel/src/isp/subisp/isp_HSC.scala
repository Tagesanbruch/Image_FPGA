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

    per_frame_vsync := io.per_isp_bus.frame_vsync
    per_frame_href := io.per_isp_bus.frame_href
    
    val in_href = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))
    val in_vsync = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))

    in_href.io.din := per_frame_href
    in_vsync.io.din := per_frame_vsync
    
    val LUT = Module(new LUT_HSC())
    LUT.io.I_mode_0 := HSC_MODE.HSC_COS.id.U
    LUT.io.I_mode_1 := HSC_MODE.HSC_SIN.id.U
    LUT.io.I_data_0 := io.I_Hue
    LUT.io.I_data_1 := io.I_Saturation
    
    val cos_data = WireInit(0.S(9.W))
    val sin_data = WireInit(0.S(9.W))

    cos_data := LUT.io.O_data_0
    sin_data := LUT.io.O_data_1
    // printf("cos_data: %d, sin_data: %d\n", cos_data, sin_data)
    val Cb_hue = WireInit(0.S(16.W))
    val Cr_hue = WireInit(0.S(16.W))

    Cb_hue := ((cos_data * (io.per_isp_bus.img_Cb - 128.U).asSInt + sin_data * (io.per_isp_bus.img_Cr - 128.U).asSInt) >> 8) + 128.S
    Cr_hue := ((cos_data * (io.per_isp_bus.img_Cr - 128.U).asSInt - sin_data * (io.per_isp_bus.img_Cb - 128.U).asSInt) >> 8) + 128.S

    val Cb_clip = WireInit(0.U(8.W))
    val Cr_clip = WireInit(0.U(8.W))

    Cb_clip := Clip(Cb_hue)
    Cr_clip := Clip(Cr_hue)

    val Cb_sat = WireInit(0.S(16.W))
    val Cr_sat = WireInit(0.S(16.W))

    Cb_sat := io.I_Saturation * (Cb_clip - 128.U).asSInt
    Cr_sat := io.I_Saturation * (Cr_clip - 128.U).asSInt

    val Cb_sat_clip = WireInit(0.U(8.W))
    val Cr_sat_clip = WireInit(0.U(8.W))

    Cb_sat_clip := Clip((Cb_sat >> 8) + 128.S)
    Cr_sat_clip := Clip((Cr_sat >> 8) + 128.S)

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.YCbCr444.id.U

    io.post_isp_bus.img_Y := io.per_isp_bus.img_Y
    io.post_isp_bus.img_Cb := Cb_sat_clip 
    io.post_isp_bus.img_Cr := Cr_sat_clip 
}
