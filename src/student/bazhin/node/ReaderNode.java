package student.bazhin.node;

import student.bazhin.pocket.PocketData;

public class ReaderNode extends ANode {

    protected ReaderNode() {
        super();
    }

    public ReaderNode(InitNode node) {
        super(node);
    }

    public void run() {

    }

    @Override
    protected PocketData waitPocket() {
        return null;
    }

    @Override
    public void sendPocket(PocketData pocket) {

    }

}
