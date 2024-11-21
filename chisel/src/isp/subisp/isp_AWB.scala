package subisp

import chisel3._
import chisel3.util._
import Modules._

class AWB(BITS: Int = 8, DELAY_NUM: Int = 5) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
        val I_r_gain = Input(UInt(8.W))
        val I_b_gain = Input(UInt(8.W))
        val I_gr_gain = Input(UInt(8.W))
        val I_gb_gain = Input(UInt(8.W))
    })

    // assuming bggr
    io.post_isp_bus := DontCare
    val hcnt = RegInit(0.U(12.W))
    val vcnt = RegInit(0.U(12.W))

    val per_frame_vsync = RegInit(false.B)
    val per_frame_href = RegInit(false.B)
    val per_img_raw = RegInit(0.U(10.W))

    per_frame_vsync := io.per_isp_bus.frame_vsync
    per_frame_href := io.per_isp_bus.frame_href
    per_img_raw := io.per_isp_bus.img_raw

    val per_img_raw_dly = RegNext(io.per_isp_bus.img_raw, 0.U)

    val oddline = dontTouch(Wire(Bool()))
    val oddcol  = dontTouch(Wire(Bool()))
    oddline := vcnt(0)
    oddcol := hcnt(0)

    val img_raw = WireInit(0.U(16.W))

    img_raw := MuxCase(
      per_img_raw,
      Seq(
        (oddline && oddcol)     -> (per_img_raw * io.I_b_gain / 100.U),
        (oddline && !oddcol)    -> (per_img_raw * io.I_gb_gain / 100.U),
        (!oddline && oddcol)    -> (per_img_raw * io.I_gr_gain / 100.U),
        (!oddline && !oddcol)   -> (per_img_raw * io.I_r_gain / 100.U),
      )
    )
    
    val in_href = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))
    val in_vsync = Module(new ShiftReg(DELAY_NUM, ShiftRegDirection.Left))

    in_href.io.din := per_frame_href
    in_vsync.io.din := per_frame_vsync
    
    val per_frame_href_dly = RegNext(in_href.io.dout, false.B)
    val img_href_neg = dontTouch(WireInit(0.U))
    img_href_neg := !per_frame_href && (per_frame_href_dly === 1.U)

    hcnt := Mux(in_href.io.dout === 1.U, hcnt + 1.U, 0.U)
    vcnt := Mux(
      in_vsync.io.dout === 0.U,
      0.U,
      Mux(img_href_neg === 1.U, vcnt + 1.U, vcnt)
    )

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.RAW_BGGR.id.U
    io.post_isp_bus.img_raw := Mux(img_raw > 255.U, 255.U, img_raw)
}
