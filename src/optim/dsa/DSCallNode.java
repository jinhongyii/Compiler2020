package optim.dsa;

import IR.Function;
import ast.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class DSCallNode {
    Function callee;
    DSHandle returnValue;
    ArrayList<DSHandle> arguments;

    public DSCallNode(Function callee, DSHandle returnValue, ArrayList<DSHandle> arguments) {
        this.callee = callee;
        this.returnValue = returnValue;
        this.arguments = arguments;
    }
}