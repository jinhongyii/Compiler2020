package optim.dsa;

import IR.*;
import IR.Types.PointerType;
import IR.instructions.*;
import optim.ConstantFolding;

import java.lang.Module;
import java.util.ArrayList;

public class LocalAnalysis implements IRVisitor{
    DSGraph graph;

    public LocalAnalysis(DSGraph graph) {
        this.graph = graph;
    }

    void run(Function function){
        DSGraph graph=new DSGraph();
        for(var arg:function.getArguments()){
            if (arg.getType() instanceof PointerType) {
                getValueNode(arg);
            }
        }
        visit(function);
        graph.markIncomplete();
    }
    DSHandle getValueNode(Value value){
        var InScalarMap=graph.scalarMap.get(value);
        if (InScalarMap != null) {
            return InScalarMap;
        }
        if (value.equals(new ConstantNull())) {
            return null;
        }
        DSNode newNode;
        if (value instanceof GlobalVariable) {
            newNode = new DSNode(((PointerType) value.getType()).getPtrType(), graph);
            newNode.setGlobal();
            newNode.globalValue.add(value);

        } else {
            newNode=new DSNode(graph);
        }
        var newhandle=new DSHandle(newNode, 0);
        graph.scalarMap.put(value,newhandle);
        return newhandle;
    }
    //used to update type info of a node(not a field)
    DSHandle updateType(DSHandle handle, Type type){
        if (!type.equals(Type.theVoidType) && type != handle.getNode().type) {
            var tmpNode = new DSNode(type,graph);
            return DSHandle.mergeCells(new DSHandle(tmpNode, 0), handle);
        } else {
            return handle;
        }
    }

    @Override
    public Object visitModule(IR.Module module) {
        return null;
    }

    @Override
    public Object visitFunction(Function function) {
        for (var bb = function.getHead(); bb != null; bb = bb.getNext()) {
            visit(bb);
        }
        return null;
    }

    @Override
    public Object visitBasicBlock(BasicBlock basicBlock) {
        for (var inst = basicBlock.getHead(); inst != null; inst = inst.getNext()) {
            visit(inst);
        }
        return null;
    }

    @Override
    public Object visitGlobalVariable(GlobalVariable globalVariable) {
        return null;
    }
    //there is no alloca after mem2reg
    @Override
    public Object visitAllocaInst(AllocaInst allocaInst) {

        return null;
    }
    //binary op inst cannot be pointer type
    @Override
    public Object visitBinaryOpInst(BinaryOpInst binaryOpInst) {
        return null;
    }

    @Override
    public Object visitBranchInst(BranchInst branchInst) {
        return null;
    }
    private void setValueNode(Value value,DSHandle handle){
        var tmp=getValueNode(value);
        DSHandle.mergeCells(tmp,handle);
    }
    @Override
    public Object visitCallInst(CallInst callInst) {
        var func=callInst.getCallee();
        if (func.getName().equals("malloc")) {
            var newNode=new DSNode(graph);
            newNode.setHeap();
            setValueNode(callInst,new DSHandle(newNode,0));
        }else {
            DSHandle returnVal=null;
            if (callInst.getType() instanceof PointerType) {
                returnVal=getValueNode(callInst);
            }
            ArrayList<DSHandle> params=new ArrayList<>();
            for (var param : callInst.getParams()) {
                if(param.getType() instanceof PointerType) {
                    params.add(getValueNode(param));
                }
            }
            graph.callSites.add(new DSCallNode(callInst.getCallee(),returnVal,params));
        }
        return null;
    }

    @Override
    public Object visitCastInst(CastInst castInst) {
        if (castInst.getType() instanceof PointerType) {
            setValueNode(castInst,getValueNode(castInst.getSource()));
        }
        return null;
    }

    @Override
    public Object visitGEPInst(GetElementPtrInst GEPInst) {
        //array indexing case
        var ptr=GEPInst.getOperands().get(0).getVal();
        var handle=getValueNode(ptr);
        if (GEPInst.getOperands().size() == 2) {
            handle = updateType(handle, ((PointerType) ptr.getType()).getPtrType());
            setValueNode(GEPInst, handle);
            handle.getNode().setArray();
        } else {
            //handle structure case
            assert GEPInst.getOperands().size()==3;
            assert ((ConstantInt)GEPInst.getOperands().get(1).getVal()).getVal()==0;
            handle=updateType(handle,((PointerType) ptr.getType()).getPtrType());
            int newfield;
            if (handle.getNode().isCollapsed()) {
                newfield = 0;
            } else {
                newfield=((ConstantInt)GEPInst.getOperands().get(2).getVal()).getVal();
            }
            setValueNode(GEPInst, new DSHandle(handle.getNode(),newfield));
        }
        return null;
    }

    @Override
    public Object visitIcmpInst(IcmpInst icmpInst) {
        return null;
    }

    @Override
    public Object visitLoadInst(LoadInst loadInst) {
        var handle = getValueNode(loadInst.getLoadTarget());
        if (handle.getNode() == null) {
            return null;
        }
        var newHandle=handle.getNode().getNonNullOutEdge(handle.field);
        newHandle.getNode().setRef();
        if (loadInst.getType() instanceof PointerType) {
            setValueNode(loadInst,newHandle);
        }
        return null;
    }

    @Override
    public Object visitPhiNode(PhiNode phiNode) {
        if (phiNode.getType() instanceof PointerType) {
            var phiDSNode=getValueNode(phiNode);
            for (int i = 0; i < phiNode.getOperands().size() / 2; i++) {
                DSHandle.mergeCells(phiDSNode,getValueNode(phiNode.getValue(i)));
            }
        }
        return null;
    }

    @Override
    public Object visitReturnInst(ReturnInst returnInst) {
        if(returnInst.getRetValue()!=null) {
            if (returnInst.getRetValue().getType() instanceof PointerType) {
                DSHandle.mergeCells(graph.pi, getValueNode(returnInst.getRetValue()));
            }
        }
        return null;
    }

    @Override
    public Object visitStoreInst(StoreInst storeInst) {
        var ptr=storeInst.getPtr();
        var value=storeInst.getStoreVal();
        var handle=getValueNode(ptr);
        assert handle.getNode()!=null;
        handle.getNode().setMod();
        if (value.getType() instanceof PointerType) {
            var ptsToHandle=handle.getNode().getNonNullOutEdge(handle.field);
            DSHandle.mergeCells(getValueNode(value),ptsToHandle);
        }
        return null;
    }

    @Override
    public Object visit(Value value) {
        return value.accept(this);
    }
}
