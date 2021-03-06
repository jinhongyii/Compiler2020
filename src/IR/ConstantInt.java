package IR;

public class ConstantInt extends Value {
    int val;

    public int getVal() {
        return val;
    }

    public ConstantInt(int val) {
        super("", Type.TheInt64,ValueType.ConstantVal);
        this.val=val;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConstantInt && ((ConstantInt) obj).val==val;
    }

    @Override
    public String toString() {
        return ((Integer)val).toString();
    }
}
