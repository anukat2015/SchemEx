package de.uni_koblenz.schemex.cache;

import java.util.Comparator;

public class LinkComparator implements Comparator<Link> {

	@Override
    public int compare( Link o1, Link o2 )
    {
        return o1.hashCode() - o2.hashCode();
    }
	

}
