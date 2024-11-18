import circt.stage._

object Elaborate extends App {
  // def npc_inst = new NPC
  def isp_inst = new ISP
  // def alu_inst = new ALU(32)
  val generator = Seq(
    chisel3.stage.ChiselGeneratorAnnotation(() => isp_inst)
  )
  (new ChiselStage).execute("--split-verilog"+:args, generator :+ CIRCTTargetAnnotation(CIRCTTarget.Verilog))

  // (new ChiselStage).execute(args, generator :+ CIRCTTargetAnnotation(CIRCTTarget.Verilog))
}
