package subisp

import chisel3._
import chisel3.util._
import Modules._

class AAF(BITS: Int = 8, DELAY_NUM: Int = 5, IMG_HDISP: Int = 1920, IMG_VDISP: Int = 1080) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
    })

    //assuming bggr
    io.post_isp_bus := DontCare

    val per_frame_vsync = RegInit(false.B)
    val per_frame_href = RegInit(false.B)
    val per_img_raw = RegInit(0.U(8.W))

    per_frame_vsync := io.per_isp_bus.frame_vsync
    per_frame_href := io.per_isp_bus.frame_href
    per_img_raw := io.per_isp_bus.img_raw

    // TODO: Logic......

    val in_href = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))
    val in_vsync = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))

    in_href.io.din := per_frame_href
    in_vsync.io.din := per_frame_vsync

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.RAW_BGGR.id.U
}