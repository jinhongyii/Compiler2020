package backend;

import Riscv.MachineBasicBlock;
import Riscv.MachineFunction;
import Riscv.MachineModule;

import java.util.HashMap;
import java.util.HashSet;

public class LivenessAnalysis {
    MachineFunction function;
    public LivenessAnalysis(MachineFunction function){
        this.function=function;
    }
    private void preprocessBlock(MachineBasicBlock block){
        block.gen.clear();
        block.kill.clear();
        for (var inst =block.getHead();inst!=null;inst=inst.getNext()) {
            //todo:fix
            var tmp=new HashSet<>(inst.getUse());
            tmp.removeAll(block.kill);
            block.gen.addAll(tmp);
            block.kill.addAll(inst.getDef());
        }
    }
    public void run(){
        boolean changed=true;
        for (var bb : function.getBasicBlocks()) {
            bb.liveIn.clear();
            bb.liveOut.clear();
            preprocessBlock(bb);
        }
        while(changed) {
            changed=false;
            for (var bb : function.getBasicBlocks()) {
                var live_in=new HashSet<>(bb.liveIn);
                var live_out=new HashSet<>(bb.liveOut);
                bb.liveIn.clear();
                bb.liveOut.clear();
                bb.liveIn.addAll(live_out);
                bb.liveIn.removeAll(bb.kill);
                bb.liveIn.addAll(bb.gen);
                for (var suc : bb.getSuccessor()) {
                    bb.liveOut.addAll(suc.liveIn);
                }
                changed|=(!live_in.equals(bb.liveIn) || !live_out.equals(bb.liveOut));
            }
        }

    }
}
