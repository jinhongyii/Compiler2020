package Riscv;

public class LI extends MachineInstruction {
    public Register rd;
    public Imm imm;
    public LI(Register rd,Imm imm){
        this.rd=rd;
        this.imm=imm;
    }

    public Register getRd() {
        return rd;
    }

    public Imm getImm() {
        return imm;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitLI(this);
    }
}
