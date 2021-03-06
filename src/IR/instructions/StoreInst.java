package IR.instructions;

import IR.*;

public class StoreInst extends Instruction {

    public StoreInst( Value storeVal,Value ptr) {
        super("",Type.theVoidType, Opcode.store);
        operands.add(new Use(storeVal,this));
        operands.add(new Use(ptr,this));
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return visitor.visitStoreInst(this);
    }
    public Value getStoreVal(){
        return operands.get(0).getVal();
    }
    public Value getPtr(){
        return operands.get(1).getVal();
    }

    @Override
    public Instruction cloneInst() {
        return new StoreInst(operands.get(0).getVal(), operands.get(1).getVal());
    }
}
