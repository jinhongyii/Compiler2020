package IR.instructions;

import IR.*;

public class CastInst extends Instruction {
    //only cast between pointers
    public CastInst(String name, Type type, Value src) {
        super(name, type,Opcode.cast);
        operands.add(new Use(src,this));
    }
    public Value getSource(){
        return operands.get(0).getVal();
    }
    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitCastInst(this);
    }
}
