import numpy as np

def generate_gamma_lut(gamma, max_value=255):
    lut = np.array([((i / max_value) ** gamma) * max_value for i in range(max_value + 1)], dtype=np.uint8)
    return lut

def write_chisel_file(luts, gammas, filename):
    with open(filename, 'w') as f:
        f.write('package LUT\n')
        f.write('import chisel3._\n\n')
        f.write('import chisel3.util._\n\n')
        f.write('object GAC_MODE extends Enumeration {\n')
        f.write('  val ')
        for i, gamma in enumerate(gammas):
            f.write(f'GAC_{str(gamma).replace(".", "_")}')
            if i < len(gammas) - 1:
                f.write(', ')
        f.write(f' = Value\n')
        f.write('}\n\n')
        f.write('class LUT_GAC extends Module {\n')
        f.write('  val io = IO(new Bundle {\n')
        f.write('    val I_mode = Input(UInt(4.W))\n')
        f.write('    val I_data_0 = Input(UInt(8.W))\n')
        f.write('    val I_data_1 = Input(UInt(8.W))\n')
        f.write('    val I_data_2 = Input(UInt(8.W))\n')
        f.write('    val I_data_3 = Input(UInt(8.W))\n')
        f.write('    val O_data_0 = Output(UInt(8.W))\n')
        f.write('    val O_data_1 = Output(UInt(8.W))\n')
        f.write('    val O_data_2 = Output(UInt(8.W))\n')
        f.write('    val O_data_3 = Output(UInt(8.W))\n')
        f.write('  })\n')
        
        for i, lut in enumerate(luts):
            f.write(f'  val lut_{str(gammas[i]).replace(".", "_")} = VecInit(Seq(\n')
            for j, value in enumerate(lut):
                if j % 16 == 0:
                    f.write('    ')
                f.write(f'{value}.U(8.W), ')
                if j % 16 == 15:
                    f.write('\n')
            f.write('  ))\n')
        
        f.write('  io.O_data_0 := MuxCase(lut_1(io.I_data_0), Seq(\n')
        for i, gamma in enumerate(gammas):
            f.write(f'    (io.I_mode === GAC_MODE.GAC_{str(gamma).replace(".", "_")}.id.U) -> lut_{str(gamma).replace(".", "_")}(io.I_data_0),\n')
        f.write('  ))\n')
        
        f.write('  io.O_data_1 := MuxCase(lut_1(io.I_data_1), Seq(\n')
        for i, gamma in enumerate(gammas):
            f.write(f'    (io.I_mode === GAC_MODE.GAC_{str(gamma).replace(".", "_")}.id.U) -> lut_{str(gamma).replace(".", "_")}(io.I_data_1),\n')
        f.write('  ))\n')

        f.write('  io.O_data_2 := MuxCase(lut_1(io.I_data_2), Seq(\n')
        for i, gamma in enumerate(gammas):
            f.write(f'    (io.I_mode === GAC_MODE.GAC_{str(gamma).replace(".", "_")}.id.U) -> lut_{str(gamma).replace(".", "_")}(io.I_data_2),\n')
        f.write('  ))\n')

        f.write('  io.O_data_3 := MuxCase(lut_1(io.I_data_3), Seq(\n')
        for i, gamma in enumerate(gammas):
            f.write(f'    (io.I_mode === GAC_MODE.GAC_{str(gamma).replace(".", "_")}.id.U) -> lut_{str(gamma).replace(".", "_")}(io.I_data_3),\n')
        f.write('  ))\n')
        f.write('}\n')

if __name__ == "__main__":
    gammas = [0.2, 0.4, 0.67, 1, 1.5, 2.2, 5]
    luts = [generate_gamma_lut(gamma) for gamma in gammas]
    write_chisel_file(luts, gammas, 'output/LUT_GAC.scala')