import numpy as np

def generate_cos_sin_lut():
    ind = np.array([i for i in range(360)])
    sin = np.sin(ind * np.pi / 180) * 255
    cos = np.cos(ind * np.pi / 180) * 255
    lut_sin = [int(sin[i]) for i in ind]
    lut_cos = [int(cos[i]) for i in ind]
    return lut_sin, lut_cos

def write_chisel_file(lut_sin, lut_cos, filename):
    with open(filename, 'w') as f:
        f.write('package LUT\n')
        f.write('import chisel3._\n\n')
        f.write('import chisel3.util._\n\n')
        f.write('object HSC_MODE extends Enumeration {\n')
        f.write('  val HSC_COS, HSC_SIN = Value\n')
        f.write('}\n\n')
        f.write('class LUT_HSC extends Module {\n')
        f.write('  val io = IO(new Bundle {\n')
        f.write('    val I_mode_0 = Input(UInt(4.W))\n')
        f.write('    val I_data_0 = Input(UInt(8.W))\n')
        f.write('    val I_mode_1 = Input(UInt(4.W))\n')
        f.write('    val I_data_1 = Input(UInt(8.W))\n')
        f.write('    val O_data_0 = Output(SInt(9.W))\n')
        f.write('    val O_data_1 = Output(SInt(9.W))\n')
        f.write('  })\n')
        
        f.write('  val lut_cos = VecInit(Seq(\n')
        for i, value in enumerate(lut_cos):
            if i % 16 == 0:
                f.write('    ')
            f.write(f'{value}.S(9.W)')
            if i != 359:
                f.write(', ')
            if i % 16 == 15:
                f.write('\n')
        f.write('  ))\n')
        f.write('   val cos2sin_0 = RegInit(0.U(8.W))\n')
        f.write('   val cos2sin_1 = RegInit(0.U(8.W))\n')
        f.write('   cos2sin_0 := Mux(io.I_data_0 > 90.U, io.I_data_0 - 90.U, io.I_data_0 + 90.U)\n')
        f.write('   cos2sin_1 := Mux(io.I_data_1 > 90.U, io.I_data_1 - 90.U, io.I_data_1 + 90.U)\n')
        f.write('  io.O_data_0 := MuxCase(lut_cos(io.I_data_0), Seq(\n')
        f.write('    (io.I_mode_0 === HSC_MODE.HSC_COS.id.U) -> lut_cos(io.I_data_0),\n')
        f.write('    (io.I_mode_0 === HSC_MODE.HSC_SIN.id.U) -> lut_cos(cos2sin_0)\n')
        f.write('  ))\n')
        f.write('  io.O_data_1 := MuxCase(lut_cos(io.I_data_1), Seq(\n')
        f.write('    (io.I_mode_1 === HSC_MODE.HSC_COS.id.U) -> lut_cos(io.I_data_1),\n')
        f.write('    (io.I_mode_1 === HSC_MODE.HSC_SIN.id.U) -> lut_cos(cos2sin_0)\n')
        f.write('  ))\n')        
        f.write('}\n')

if __name__ == "__main__":
    lut_sin, lut_cos = generate_cos_sin_lut()
    write_chisel_file(lut_sin, lut_cos, 'output/LUT_HSC.scala')