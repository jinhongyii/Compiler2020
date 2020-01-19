package ast;

import frontend.ASTVisitor;
import semantic.TypeChecker;

import java.util.ArrayList;

public class MethodCallExpr extends Expr {
    Expr name;
    ArrayList<Expr> arguments;

    public Expr getName() {
        return name;
    }

    public ArrayList<Expr> getArguments() {
        return arguments;
    }

    public MethodCallExpr(Expr name, ArrayList<Expr> arguments){
        this.name=name;
        this.arguments=arguments;
    }
    void addarguments(Expr argument){
        arguments.add(argument);
    }

    @Override
    public Object accept(ASTVisitor visitor) throws TypeChecker.semanticException {
        return visitor.visitMethodCallExpr(this);
    }
}
