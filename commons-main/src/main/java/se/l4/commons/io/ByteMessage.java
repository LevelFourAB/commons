package se.l4.commons.io;

import java.util.function.Predicate;

public interface ByteMessage
{
	long getTag();
	
	Bytes getData();
	
	static Predicate<ByteMessage> tag(int tag)
	{
		return (in) -> in.getTag() == tag;
	}
}
