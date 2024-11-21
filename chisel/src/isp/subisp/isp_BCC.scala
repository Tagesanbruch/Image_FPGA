package subisp

import chisel3._
import chisel3.util._
import Modules._
import LUT._

class BCC(BITS: Int = 8, DELAY_NUM: Int = 5) extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
        val I_Brightness_Offset = Input(SInt(8.W))
        val I_Contrast_Gain = Input(UInt(8.W))
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
    
        //     y_image = data['y_image'].astype(np.int32)

        // bcc_y_image = np.clip(y_image + self.brightness_offset, 0, self.cfg.saturation_values.sdr)

        // y_median = np.median(bcc_y_image).astype(np.int32)
        // bcc_y_image = np.right_shift((bcc_y_image - y_median) * self.contrast_gain, 8) + y_median
        // bcc_y_image = np.clip(bcc_y_image, 0, self.cfg.saturation_values.sdr)

        // data['y_image'] = bcc_y_image.astype(np.uint8)

    val BCC_Y = WireInit(0.U(8.W))
    val BCC_Y2 = WireInit(0.U(8.W))
    
    BCC_Y := ClipAdd(io.per_isp_bus.img_Y, io.I_Brightness_Offset)

    BCC_Y2 := ClipAdd(BCC_Y, (io.I_Contrast_Gain * (BCC_Y.asSInt - 127.S)) >> 8)

    io.post_isp_bus.frame_href := in_href.io.dout
    io.post_isp_bus.frame_vsync := in_vsync.io.dout
    io.post_isp_bus.frame_mode := ISPMode.YCbCr444.id.U

    io.post_isp_bus.img_Y := BCC_Y2
    io.post_isp_bus.img_Cb := io.per_isp_bus.img_Cb
    io.post_isp_bus.img_Cr := io.per_isp_bus.img_Cr
}
