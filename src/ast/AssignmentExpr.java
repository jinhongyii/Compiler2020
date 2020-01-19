package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

public class AssignmentExpr extends Expr{
    public Expr getLval() {
        return lval;
    }

    public void setLval(Expr lval) {
        this.lval = lval;
    }

    public Expr getRval() {
        return rval;
    }

    public void setRval(Expr rval) {
        this.rval = rval;
    }

    Expr lval;
    Expr rval;
    public AssignmentExpr(Expr lval,Expr rval){
        this.lval=lval;
        this.rval=rval;
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitAssignmentExpr(this);
    }
}
