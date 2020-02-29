package optim.dsa;

import IR.*;
import IR.Module;
import IR.instructions.CallInst;
import optim.AliasAnalysis;
import optim.LICM;

import java.util.HashMap;

public class DSA extends AliasAnalysis {
    private Local local;
    private BottomUp bottomUp;
    private Module module;
    public DSA(Module module){
        this.module=module;
    }
    @Override
    public AliasResult alias(Value v1, Value v2) {
        if (v1 == v2) {
            return AliasResult.MustAlias;
        }
        var mainGraph=bottomUp.graphs.get((Function)module.getSymbolTable().get("main"));
        var handle1=mainGraph.scalarMap.get(v1);
        var handle2=mainGraph.scalarMap.get(v2);
        var node1=handle1.getNode();
        var node2=handle2.getNode();

        if (node1 != node2) {
            return AliasResult.NoAlias;
        }

        return super.alias(v1,v2);
    }
//    DSGraph getGraph(Value value){
//        if (value instanceof Instruction) {
//            return graphs.get(((Instruction) value).getParent().getParent());
//        } else if (value instanceof Argument) {
//            return graphs.get(((Argument) value).getParent());
//        }
//        return null;
//    }
    public void run(Module module){
        local=new Local();
        local.run(module);
        bottomUp=new BottomUp(local);
        bottomUp.run(module);
    }

    @Override
    public ModRef getCallModRefInfo(CallInst callInst, Value value) {
        //todo:wrong
//        var calleeF=callInst.getCallee();
//        if (calleeF.isExternalLinkage()) {
//            return super.getCallModRefInfo(callInst,value);
//        }
//        var graph = bottomUp.graphs.get(calleeF);
//        var handle=graph.scalarMap.get(value);
//        if (handle != null) {
//            var node=handle.getNode();
//            if (node.isMod() && node.isRef()) {
//                return ModRef.ModRef;
//            } else if (node.isMod()) {
//                return ModRef.Mod;
//            } else if (node.isRef()) {
//                return ModRef.Ref;
//            }
//        } else {
//            return ModRef.NoModRef;
//        }
        return super.getCallModRefInfo(callInst,value);
    }
}
