package student.bazhin.node;

import student.bazhin.pocket.PocketData;

public class ViewerNode extends ANode {

    protected ViewerNode() {
        super();
    }

    public ViewerNode(InitNode node) {
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
