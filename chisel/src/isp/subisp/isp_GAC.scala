package subisp

import chisel3._
import chisel3.util._
import Modules._
import LUT._

class GAC(BITS: Int = 8, DELAY_NUM: Int = 5) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
        val I_GAC_mode = Input(UInt(8.W))
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
    
    val LUT = Module(new LUT_GAC())
    LUT.io.I_mode := io.I_GAC_mode
    LUT.io.I_data_0 := io.per_isp_bus.img_red
    LUT.io.I_data_1 := io.per_isp_bus.img_green
    LUT.io.I_data_2 := io.per_isp_bus.img_blue
    LUT.io.I_data_3 := DontCare
    
    io.post_isp_bus.img_red := LUT.io.O_data_0
    io.post_isp_bus.img_green := LUT.io.O_data_1
    io.post_isp_bus.img_blue := LUT.io.O_data_2

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.RGB888.id.U
}
