import numpy as np

def generate_cos_sin_lut():
    ind = np.array([i for i in range(360)])
    sin = (np.sin(ind * np.pi / 180) + 1) / 2 * 255
    cos = (np.cos(ind * np.pi / 180) + 1) / 2 * 255
    lut_sin = [round(sin[i]) for i in ind]
    lut_cos = [round(cos[i]) for i in ind]
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
        f.write('    val O_data_0 = Output(UInt(8.W))\n')
        f.write('    val O_data_1 = Output(UInt(8.W))\n')
        f.write('  })\n')
        
        f.write('  val lut_sin = VecInit(Seq(\n')
        for i, value in enumerate(lut_sin):
            if i % 16 == 0:
                f.write('    ')
            f.write(f'{value}.U(8.W), ')
            if i % 16 == 15:
                f.write('\n')
        f.write('  ))\n')
        
        f.write('  val lut_cos = VecInit(Seq(\n')
        for i, value in enumerate(lut_cos):
            if i % 16 == 0:
                f.write('    ')
            f.write(f'{value}.U(8.W), ')
            if i % 16 == 15:
                f.write('\n')
        f.write('  ))\n')
        
        f.write('  io.O_data_0 := MuxLookup(io.I_mode_0, lut_cos(io.I_data), Seq(\n')
        f.write('    HSC_MODE.HSC_COS.id.U -> lut_cos(io.I_data_0),\n')
        f.write('    HSC_MODE.HSC_SIN.id.U -> lut_sin(io.I_data_0)\n')
        f.write('  ))\n')
        f.write('  io.O_data_1 := MuxLookup(io.I_mode_1, lut_cos(io.I_data), Seq(\n')
        f.write('    HSC_MODE.HSC_COS.id.U -> lut_cos(io.I_data_1),\n')
        f.write('    HSC_MODE.HSC_SIN.id.U -> lut_sin(io.I_data_1)\n')
        f.write('  ))\n')
        f.write('}\n')

if __name__ == "__main__":
    lut_sin, lut_cos = generate_cos_sin_lut()
    write_chisel_file(lut_sin, lut_cos, 'output/LUT_HSC.scala')