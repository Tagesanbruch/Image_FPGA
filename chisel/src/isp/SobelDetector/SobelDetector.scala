import chisel3._
import chisel3.util._
import chisel3.SpecifiedDirection.Flip
import Modules._
import subisp._

class SobelDetector(IMG_HDISP: Int = 1920, IMG_VDISP: Int = 1080, DELAY_NUM: Int = 5)
    extends Module {
    val io = IO(new Bundle {
        val thresh = Input(UInt(8.W))
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
    })

    io.post_isp_bus := DontCare
    val matrix = Module(
      new ShiftMatrix(3, IMG_HDISP, 8, IMG_HDISP, IMG_VDISP, DELAY_NUM)
    )

    val per_img_vsync = RegInit(false.B)
    val per_img_href = RegInit(false.B)
    val per_img_gray = RegInit(0.U(8.W))

    per_img_vsync := io.per_isp_bus.frame_vsync
    per_img_href := io.per_isp_bus.frame_href
    per_img_gray := io.per_isp_bus.img_Y

    matrix.io.per_img_vsync := per_img_vsync
    matrix.io.per_img_href  := per_img_href
    matrix.io.per_img_gray  := per_img_gray

    val Gx_data_tmp1 = Reg(UInt(10.W))
    val Gx_data_tmp2 = Reg(UInt(10.W))
    val Gy_data_tmp1 = Reg(UInt(10.W))
    val Gy_data_tmp2 = Reg(UInt(10.W))
    val Gx_data = Reg(SInt(11.W))
    val Gy_data = Reg(SInt(11.W))
    val Gx_square_data = Reg(SInt(22.W))
    val Gy_square_data = Reg(SInt(22.W))
    val G_square_data = Reg(UInt(21.W))
    val G_data = Wire(UInt(11.W))
    Gx_data_tmp1 := matrix.io
        .matrix(0)(2) + (matrix.io.matrix(1)(2) << 1) + matrix.io.matrix(2)(2)
    Gx_data_tmp2 := matrix.io
        .matrix(0)(0) + (matrix.io.matrix(1)(0) << 1) + matrix.io.matrix(2)(0)
    Gy_data_tmp1 := matrix.io
        .matrix(2)(0) + (matrix.io.matrix(2)(1) << 1) + matrix.io.matrix(2)(2)
    Gy_data_tmp2 := matrix.io
        .matrix(0)(0) + (matrix.io.matrix(0)(1) << 1) + matrix.io.matrix(0)(2)
    Gx_data := Gx_data_tmp1.asSInt - Gx_data_tmp2.asSInt
    Gy_data := Gy_data_tmp1.asSInt - Gy_data_tmp2.asSInt
    Gx_square_data := Gx_data * Gx_data
    Gy_square_data := Gy_data * Gy_data
    G_square_data := (Gx_square_data + Gy_square_data).asUInt

    val sqrt = Module(new Sqrt)
    sqrt.io.din := G_square_data
    sqrt.io.din_valid := true.B
    G_data := sqrt.io.dout

    //  lag DELAY_PERIOD clocks signal sync

    val DELAY_PERIOD = DELAY_NUM + 16
    val vsync_ShiftReg = Module(new ShiftReg(DELAY_PERIOD + 2 * IMG_HDISP, ShiftRegDirection.Left))// 2 lines
    val href_ShiftReg = Module(new ShiftReg(DELAY_PERIOD + 2 * IMG_HDISP, ShiftRegDirection.Left))// 2 lines
    val edge_flag_ShiftReg = Module(new ShiftReg(DELAY_PERIOD, ShiftRegDirection.Left))

    vsync_ShiftReg.io.din := matrix.io.matrix_img_vsync
    href_ShiftReg.io.din := matrix.io.matrix_img_href
    edge_flag_ShiftReg.io.din := matrix.io.matrix_top_edge_flag | matrix.io.matrix_bottom_edge_flag | matrix.io.matrix_left_edge_flag | matrix.io.matrix_right_edge_flag

    val vsync_ShiftReg_Out = WireInit(0.U(1.W))
    val href_ShiftReg_Out = WireInit(0.U(1.W))
    val edge_flag_ShiftReg_Out = WireInit(0.U(1.W))
    val clken_ShiftReg_Out = WireInit(0.U(1.W))

    vsync_ShiftReg_Out := vsync_ShiftReg.io.dout
    href_ShiftReg_Out := href_ShiftReg.io.dout
    edge_flag_ShiftReg_Out := edge_flag_ShiftReg.io.dout
    // clken_ShiftReg_Out := clken_ShiftReg2.io.dout

    io.post_isp_bus.img_bit := Mux(
      edge_flag_ShiftReg_Out === 1.U,
      false.B,
      Mux(G_data > io.thresh, true.B, false.B)
    )
    io.post_isp_bus.frame_vsync := vsync_ShiftReg_Out
    io.post_isp_bus.frame_href  := href_ShiftReg_Out
    io.post_isp_bus.img_Y       := Mux(io.post_isp_bus.img_bit === 1.U, 255.U, 0.U)
    io.post_isp_bus.frame_mode  := ISPMode.Gray.id.U
}
