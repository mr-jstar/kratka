package graphs;

import java.io.IOException;
import java.util.Set;

/**
 *
 * @author jstar
 */
public interface Graph {

    public int getNumNodes();

    public Set<Edge> getConnectionsList(int nodeNumber);

    public void save(String path) throws IOException;

}