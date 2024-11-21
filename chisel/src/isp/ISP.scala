import chisel3._
import chisel3.util._
import subisp._

object ISPRegOffset {
    val ISP_CTRL = 0x00
    val ISP_STATUS = 0x04
    val ISP_IMG_WIDTH = 0x08
    val ISP_IMG_HEIGHT = 0x0C
    val ISP_IMG_HDISP = 0x10
    val ISP_IMG_VDISP = 0x14
    // val ISP_IMG_FORMAT = 0x18
    val ISP_BLC_LO = 0x18
    val ISP_BLC_HI = 0x1C
    val ISP_CCM_LO = 0x20
    val ISP_CCM_MI = 0x24
    val ISP_CCM_HI = 0x28
    val ISP_AWB = 0x2C
    val ISP_GAC = 0x30
    val ISP_HSC = 0x34
    val ISP_BCC = 0x38
    // val ISP_CSC = 0x24
    // val ISP_BLC = 0x28
    // val ISP_DPC = 0x2C
    // val ISP_SOBEL = 0x30
    val ISP_SOBEL_THRESH = 0x50
}

class ISP extends Module {
    val io = IO(new Bundle {
        val per_isp_bus = Flipped(new isp_bus)
        val post_isp_bus = new isp_bus
    })

    io.post_isp_bus := DontCare

    val IMG_WIDTH = 1920
    val IMG_HEIGHT = 1080

    val BLC = Module(new BLC(8, 2, IMG_WIDTH, IMG_HEIGHT))

    val CFA_2 = Module(new CFA(8, 2, IMG_WIDTH, IMG_HEIGHT))

    val AWB = Module(new AWB(8, 2))
    
    val CCM = Module(new CCM(8, 2))

    val CSC = Module(new CSC(8, 2))

    val GAC = Module(new GAC(8, 2))

    val HSC = Module(new HSC(8, 2))

    val BCC = Module(new BCC(8, 2))

    val YCbCr2RGB = Module(new YCbCr2RGB(8, 2))

    val SobelDetector = Module(new SobelDetector(IMG_WIDTH, IMG_HEIGHT, 10))

    // APB Addr: 0xF01F0000 - 0xF01F00FF

    val resetValues = VecInit(
      Seq(
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //BLC_LO, 0, 3, 2, 2
        0x00000000.U(32.W), //BLC_HI, 2, 3
        0x00FDFD16.U(32.W), //CCM_LO, -3, -3, 22
        0x00FE14FE.U(32.W), //CCM_MI, -2, 20, -2
        0x0016FDFD.U(32.W), //CCM_HI, 22, -3, -3
        BigInt("80646480", 16).U(32.W), //AWB, 128, 100, 100, 128
        0x00000002.U(32.W), //GAC, 0
        0x00000000.U(32.W), //HSC
        0x00000000.U(32.W), //BCC
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x0000005A.U(32.W), //SOBEL_THRESH, 90
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W), //
        0x00000000.U(32.W)  //
      )
    )

    val ConfigRegFile = RegInit(resetValues)

    def ConfigReg(x: Int): UInt = {
        ConfigRegFile(x / 4)
    }

    BLC.io.per_isp_bus <> io.per_isp_bus

    BLC.io.I_r_offset := ConfigReg(ISPRegOffset.ISP_BLC_LO)(7, 0).asSInt
    BLC.io.I_b_offset := ConfigReg(ISPRegOffset.ISP_BLC_LO)(15, 8).asSInt
    BLC.io.I_gr_offset := ConfigReg(ISPRegOffset.ISP_BLC_LO)(23, 16).asSInt
    BLC.io.I_gb_offset := ConfigReg(ISPRegOffset.ISP_BLC_LO)(31, 24).asSInt
    BLC.io.I_alpha := ConfigReg(ISPRegOffset.ISP_BLC_HI)(7, 0)
    BLC.io.I_beta := ConfigReg(ISPRegOffset.ISP_BLC_HI)(15, 8)

    AWB.io.per_isp_bus <> BLC.io.post_isp_bus

    AWB.io.I_b_gain := ConfigReg(ISPRegOffset.ISP_AWB)(7, 0)
    AWB.io.I_gb_gain := ConfigReg(ISPRegOffset.ISP_AWB)(15, 8)
    AWB.io.I_gr_gain := ConfigReg(ISPRegOffset.ISP_AWB)(23, 16)
    AWB.io.I_r_gain := ConfigReg(ISPRegOffset.ISP_AWB)(31, 24)

    CFA_2.io.per_isp_bus <> AWB.io.post_isp_bus

    val CFA = Module(new CFA_Verilog_TOP(8, 5, IMG_WIDTH, IMG_HEIGHT))
    CFA.io.per_isp_bus <> AWB.io.post_isp_bus

    CCM.io.per_isp_bus <> CFA.io.post_isp_bus

    CCM.io.I_m_rr := ConfigReg(ISPRegOffset.ISP_CCM_LO)(7, 0).asSInt
    CCM.io.I_m_rg := ConfigReg(ISPRegOffset.ISP_CCM_LO)(15, 8).asSInt
    CCM.io.I_m_rb := ConfigReg(ISPRegOffset.ISP_CCM_LO)(23, 16).asSInt
    CCM.io.I_m_gr := ConfigReg(ISPRegOffset.ISP_CCM_MI)(7, 0).asSInt
    CCM.io.I_m_gg := ConfigReg(ISPRegOffset.ISP_CCM_MI)(15, 8).asSInt
    CCM.io.I_m_gb := ConfigReg(ISPRegOffset.ISP_CCM_MI)(23, 16).asSInt
    CCM.io.I_m_br := ConfigReg(ISPRegOffset.ISP_CCM_HI)(7, 0).asSInt
    CCM.io.I_m_bg := ConfigReg(ISPRegOffset.ISP_CCM_HI)(15, 8).asSInt
    CCM.io.I_m_bb := ConfigReg(ISPRegOffset.ISP_CCM_HI)(23, 16).asSInt

    GAC.io.per_isp_bus <> CCM.io.post_isp_bus

    GAC.io.I_GAC_mode := ConfigReg(ISPRegOffset.ISP_GAC)(7, 0)

    CSC.io.per_isp_bus <> GAC.io.post_isp_bus

    HSC.io.per_isp_bus <> CSC.io.post_isp_bus

    HSC.io.I_Hue := ConfigReg(ISPRegOffset.ISP_HSC)(7, 0)
    HSC.io.I_Saturation := ConfigReg(ISPRegOffset.ISP_HSC)(15, 8)

    BCC.io.per_isp_bus <> HSC.io.post_isp_bus

    BCC.io.I_Brightness_Offset := ConfigReg(ISPRegOffset.ISP_BCC)(7, 0).asSInt
    BCC.io.I_Contrast_Gain := ConfigReg(ISPRegOffset.ISP_BCC)(15, 8)


    SobelDetector.io.per_isp_bus <> BCC.io.post_isp_bus

    SobelDetector.io.thresh := ConfigReg(ISPRegOffset.ISP_SOBEL_THRESH)(7, 0)

    // val CSC_V = Module(new CSC_V(8, 5))

    // CSC_V.io.per_isp_bus <> GAC.io.post_isp_bus

    YCbCr2RGB.io.per_isp_bus <> CSC.io.post_isp_bus


    // io.post_isp_bus <> SobelDetector.io.post_isp_bus
    // io.post_isp_bus <> CSC.io.post_isp_bus
    // io.post_isp_bus <> CFA.io.post_isp_bus
    // io.post_isp_bus <> GAC.io.post_isp_bus
    io.post_isp_bus <> YCbCr2RGB.io.post_isp_bus
}
