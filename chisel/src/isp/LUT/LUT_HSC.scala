package LUT
import chisel3._

import chisel3.util._

object HSC_MODE extends Enumeration {
  val HSC_COS, HSC_SIN = Value
}

class LUT_HSC extends Module {
  val io = IO(new Bundle {
    val I_mode_0 = Input(UInt(4.W))
    val I_data_0 = Input(UInt(8.W))
    val I_mode_1 = Input(UInt(4.W))
    val I_data_1 = Input(UInt(8.W))
    val O_data_0 = Output(SInt(9.W))
    val O_data_1 = Output(SInt(9.W))
  })
  val lut_cos = VecInit(Seq(
    255.S(9.W), 254.S(9.W), 254.S(9.W), 254.S(9.W), 254.S(9.W), 254.S(9.W), 253.S(9.W), 253.S(9.W), 252.S(9.W), 251.S(9.W), 251.S(9.W), 250.S(9.W), 249.S(9.W), 248.S(9.W), 247.S(9.W), 246.S(9.W), 
    245.S(9.W), 243.S(9.W), 242.S(9.W), 241.S(9.W), 239.S(9.W), 238.S(9.W), 236.S(9.W), 234.S(9.W), 232.S(9.W), 231.S(9.W), 229.S(9.W), 227.S(9.W), 225.S(9.W), 223.S(9.W), 220.S(9.W), 218.S(9.W), 
    216.S(9.W), 213.S(9.W), 211.S(9.W), 208.S(9.W), 206.S(9.W), 203.S(9.W), 200.S(9.W), 198.S(9.W), 195.S(9.W), 192.S(9.W), 189.S(9.W), 186.S(9.W), 183.S(9.W), 180.S(9.W), 177.S(9.W), 173.S(9.W), 
    170.S(9.W), 167.S(9.W), 163.S(9.W), 160.S(9.W), 156.S(9.W), 153.S(9.W), 149.S(9.W), 146.S(9.W), 142.S(9.W), 138.S(9.W), 135.S(9.W), 131.S(9.W), 127.S(9.W), 123.S(9.W), 119.S(9.W), 115.S(9.W), 
    111.S(9.W), 107.S(9.W), 103.S(9.W), 99.S(9.W), 95.S(9.W), 91.S(9.W), 87.S(9.W), 83.S(9.W), 78.S(9.W), 74.S(9.W), 70.S(9.W), 65.S(9.W), 61.S(9.W), 57.S(9.W), 53.S(9.W), 48.S(9.W), 
    44.S(9.W), 39.S(9.W), 35.S(9.W), 31.S(9.W), 26.S(9.W), 22.S(9.W), 17.S(9.W), 13.S(9.W), 8.S(9.W), 4.S(9.W), 0.S(9.W), -4.S(9.W), -8.S(9.W), -13.S(9.W), -17.S(9.W), -22.S(9.W), 
    -26.S(9.W), -31.S(9.W), -35.S(9.W), -39.S(9.W), -44.S(9.W), -48.S(9.W), -53.S(9.W), -57.S(9.W), -61.S(9.W), -65.S(9.W), -70.S(9.W), -74.S(9.W), -78.S(9.W), -83.S(9.W), -87.S(9.W), -91.S(9.W), 
    -95.S(9.W), -99.S(9.W), -103.S(9.W), -107.S(9.W), -111.S(9.W), -115.S(9.W), -119.S(9.W), -123.S(9.W), -127.S(9.W), -131.S(9.W), -135.S(9.W), -138.S(9.W), -142.S(9.W), -146.S(9.W), -149.S(9.W), -153.S(9.W), 
    -156.S(9.W), -160.S(9.W), -163.S(9.W), -167.S(9.W), -170.S(9.W), -173.S(9.W), -177.S(9.W), -180.S(9.W), -183.S(9.W), -186.S(9.W), -189.S(9.W), -192.S(9.W), -195.S(9.W), -198.S(9.W), -200.S(9.W), -203.S(9.W), 
    -206.S(9.W), -208.S(9.W), -211.S(9.W), -213.S(9.W), -216.S(9.W), -218.S(9.W), -220.S(9.W), -223.S(9.W), -225.S(9.W), -227.S(9.W), -229.S(9.W), -231.S(9.W), -232.S(9.W), -234.S(9.W), -236.S(9.W), -238.S(9.W), 
    -239.S(9.W), -241.S(9.W), -242.S(9.W), -243.S(9.W), -245.S(9.W), -246.S(9.W), -247.S(9.W), -248.S(9.W), -249.S(9.W), -250.S(9.W), -251.S(9.W), -251.S(9.W), -252.S(9.W), -253.S(9.W), -253.S(9.W), -254.S(9.W), 
    -254.S(9.W), -254.S(9.W), -254.S(9.W), -254.S(9.W), -255.S(9.W), -254.S(9.W), -254.S(9.W), -254.S(9.W), -254.S(9.W), -254.S(9.W), -253.S(9.W), -253.S(9.W), -252.S(9.W), -251.S(9.W), -251.S(9.W), -250.S(9.W), 
    -249.S(9.W), -248.S(9.W), -247.S(9.W), -246.S(9.W), -245.S(9.W), -243.S(9.W), -242.S(9.W), -241.S(9.W), -239.S(9.W), -238.S(9.W), -236.S(9.W), -234.S(9.W), -232.S(9.W), -231.S(9.W), -229.S(9.W), -227.S(9.W), 
    -225.S(9.W), -223.S(9.W), -220.S(9.W), -218.S(9.W), -216.S(9.W), -213.S(9.W), -211.S(9.W), -208.S(9.W), -206.S(9.W), -203.S(9.W), -200.S(9.W), -198.S(9.W), -195.S(9.W), -192.S(9.W), -189.S(9.W), -186.S(9.W), 
    -183.S(9.W), -180.S(9.W), -177.S(9.W), -173.S(9.W), -170.S(9.W), -167.S(9.W), -163.S(9.W), -160.S(9.W), -156.S(9.W), -153.S(9.W), -149.S(9.W), -146.S(9.W), -142.S(9.W), -138.S(9.W), -135.S(9.W), -131.S(9.W), 
    -127.S(9.W), -123.S(9.W), -119.S(9.W), -115.S(9.W), -111.S(9.W), -107.S(9.W), -103.S(9.W), -99.S(9.W), -95.S(9.W), -91.S(9.W), -87.S(9.W), -83.S(9.W), -78.S(9.W), -74.S(9.W), -70.S(9.W), -65.S(9.W), 
    -61.S(9.W), -57.S(9.W), -53.S(9.W), -48.S(9.W), -44.S(9.W), -39.S(9.W), -35.S(9.W), -31.S(9.W), -26.S(9.W), -22.S(9.W), -17.S(9.W), -13.S(9.W), -8.S(9.W), -4.S(9.W), 0.S(9.W), 4.S(9.W), 
    8.S(9.W), 13.S(9.W), 17.S(9.W), 22.S(9.W), 26.S(9.W), 31.S(9.W), 35.S(9.W), 39.S(9.W), 44.S(9.W), 48.S(9.W), 53.S(9.W), 57.S(9.W), 61.S(9.W), 65.S(9.W), 70.S(9.W), 74.S(9.W), 
    78.S(9.W), 83.S(9.W), 87.S(9.W), 91.S(9.W), 95.S(9.W), 99.S(9.W), 103.S(9.W), 107.S(9.W), 111.S(9.W), 115.S(9.W), 119.S(9.W), 123.S(9.W), 127.S(9.W), 131.S(9.W), 135.S(9.W), 138.S(9.W), 
    142.S(9.W), 146.S(9.W), 149.S(9.W), 153.S(9.W), 156.S(9.W), 160.S(9.W), 163.S(9.W), 167.S(9.W), 170.S(9.W), 173.S(9.W), 177.S(9.W), 180.S(9.W), 183.S(9.W), 186.S(9.W), 189.S(9.W), 192.S(9.W), 
    195.S(9.W), 198.S(9.W), 200.S(9.W), 203.S(9.W), 206.S(9.W), 208.S(9.W), 211.S(9.W), 213.S(9.W), 216.S(9.W), 218.S(9.W), 220.S(9.W), 223.S(9.W), 225.S(9.W), 227.S(9.W), 229.S(9.W), 231.S(9.W), 
    232.S(9.W), 234.S(9.W), 236.S(9.W), 238.S(9.W), 239.S(9.W), 241.S(9.W), 242.S(9.W), 243.S(9.W), 245.S(9.W), 246.S(9.W), 247.S(9.W), 248.S(9.W), 249.S(9.W), 250.S(9.W), 251.S(9.W), 251.S(9.W), 
    252.S(9.W), 253.S(9.W), 253.S(9.W), 254.S(9.W), 254.S(9.W), 254.S(9.W), 254.S(9.W), 254.S(9.W)  ))
   val cos2sin_0 = RegInit(0.U(8.W))
   val cos2sin_1 = RegInit(0.U(8.W))
   cos2sin_0 := Mux(io.I_data_0 > 90.U, io.I_data_0 - 90.U, io.I_data_0 + 90.U)
   cos2sin_1 := Mux(io.I_data_1 > 90.U, io.I_data_1 - 90.U, io.I_data_1 + 90.U)
  io.O_data_0 := MuxCase(lut_cos(io.I_data_0), Seq(
    (io.I_mode_0 === HSC_MODE.HSC_COS.id.U) -> lut_cos(io.I_data_0),
    (io.I_mode_0 === HSC_MODE.HSC_SIN.id.U) -> lut_cos(cos2sin_0)
  ))
  io.O_data_1 := MuxCase(lut_cos(io.I_data_1), Seq(
    (io.I_mode_1 === HSC_MODE.HSC_COS.id.U) -> lut_cos(io.I_data_1),
    (io.I_mode_1 === HSC_MODE.HSC_SIN.id.U) -> lut_cos(cos2sin_0)
  ))
}
