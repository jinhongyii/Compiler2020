package IR;

public class Instruction extends User {

    Instruction prev=null;
    Instruction next=null;
    public Instruction(String name, Type type,Opcode opcode) {
        super(name, type, ValueType.InstructionVal);
        this.opcode=opcode;
    }

    public enum Opcode{
        alloca,load,store,ret,br,call,getelementptr,EQ,NE,LE,GE,LT,GT,phi,add,
        sub,mul,div,rem,and,or,xor,shl,shr,cast
    }
    BasicBlock parent;
    private Opcode opcode;

    public BasicBlock getParent() {
        return parent;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public Object accept(IRVisitor visitor) {
        return null;
    }

    public boolean isTerminator(){
        return opcode==Opcode.ret || opcode==Opcode.br;
    }

    public Instruction getPrev() {
        return prev;
    }

    public Instruction getNext() {
        return next;
    }

    public void setNextInstruction(Instruction instruction) {
        this.next=instruction;
        instruction.prev=this;
    }
}
